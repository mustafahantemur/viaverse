package app.viaverse.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import app.viaverse.identity.auth.infrastructure.adapter.out.seed.LocalTestUserSeeder;
import app.viaverse.identity.auth.infrastructure.security.JwtAccessTokenService;
import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.account.domain.AccountStatusEnum;
import app.viaverse.identity.account.infrastructure.adapter.out.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.account.infrastructure.adapter.out.persistence.repository.IdentityAccountJpaRepository;
import app.viaverse.identity.config.AuthConfiguration;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.auth.domain.enums.SmsProviderEnum;
import app.viaverse.identity.support.IdentityTestcontainers;
import app.viaverse.shared.kernel.error.TechnicalException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;
import java.util.Set;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "viaverse.auth.otp.max-attempts=2",
                "viaverse.auth.rate-limit.auth-start.identifier-max-attempts=2",
                "viaverse.auth.rate-limit.auth-start.ip-max-attempts=2",
                "viaverse.auth.rate-limit.otp-verify.flow-max-attempts=5",
                "viaverse.auth.rate-limit.otp-verify.ip-max-attempts=20",
                "viaverse.auth.rate-limit.resend.cooldown-seconds=60",
                "viaverse.auth.rate-limit.lockout.duration-seconds=900"
        }
)
@ActiveProfiles("test")
@ContextConfiguration(initializers = IdentityAuthIntegrationTest.FlywayInitializer.class)
class IdentityAuthIntegrationTest {
    private static final String DEBUG_OTP = "111111";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", IdentityTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", IdentityTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", IdentityTestcontainers.POSTGRES::getPassword);
        registry.add("spring.data.redis.host", IdentityTestcontainers.VALKEY::getHost);
        registry.add("spring.data.redis.port",
                () -> IdentityTestcontainers.VALKEY.getMappedPort(6379));
        registry.add("spring.kafka.bootstrap-servers",
                IdentityTestcontainers.KAFKA::getBootstrapServers);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LocalTestUserSeeder localTestUserSeeder;

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private IdentityAccountJpaRepository accountRepository;

    @Autowired
    private JwtAccessTokenService jwtAccessTokenService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void cleanDatabase() {
        try (var connection = redisConnectionFactory.getConnection()) {
            connection.serverCommands().flushDb();
        }
        jdbcTemplate.execute("DELETE FROM auth_rate_limit_bucket");
        jdbcTemplate.execute("DELETE FROM admin_invitation");
        jdbcTemplate.execute("DELETE FROM consent_record");
        jdbcTemplate.execute("DELETE FROM auth_refresh_token");
        jdbcTemplate.execute("DELETE FROM auth_session");
        jdbcTemplate.execute("DELETE FROM auth_otp_challenge");
        jdbcTemplate.execute("DELETE FROM auth_login_flow");
        jdbcTemplate.execute("DELETE FROM identity_identifier");
        jdbcTemplate.execute("DELETE FROM identity_account_role");
        jdbcTemplate.execute("DELETE FROM identity_account");
        jdbcTemplate.execute("DELETE FROM audit_log");
    }

    @Test
    void startAuthWithEmail() {
        ResponseEntity<Map> response = startAuth(newEmail());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("identifierType", "EMAIL");
        assertThat(response.getBody()).containsEntry("nextStep", "OTP_REQUIRED");
        assertThat(response.getBody()).doesNotContainKey("debugOtp");
        assertThat(response.getBody()).containsKey("flowId");
        assertThat(response.getBody()).containsKey("expiresAt");
    }

    @Test
    void fixedOtpWorksInLocalTest() {
        Map<String, Object> verified = verifyNewUser(newEmail());

        assertThat(verified).containsEntry("nextStep", "REGISTRATION_REQUIRED");
    }

    @Test
    void debugAdapterIsActiveForLocalTestFlowButDoesNotLeakOtpToApi() {
        ResponseEntity<Map> response = startAuth(newEmail());

        assertThat(authProperties.getDebug().isEnabled()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).doesNotContainKey("debugOtp");
    }

    @Test
    void verifyOtpForNewUserReturnsRegistrationRequired() {
        Map<String, Object> verified = verifyNewUser(newEmail());

        assertThat(verified).containsEntry("nextStep", "REGISTRATION_REQUIRED");
        assertThat(verified).containsKey("registrationToken");
        assertThat(verified).containsKey("registrationExpiresAt");
    }

    @Test
    void completeRegistrationReturnsTokens() {
        Map<String, Object> registered = registerNewUser(newEmail());

        assertThat(registered).containsEntry("nextStep", "AUTHENTICATED");
        assertThat(registered.get("accessToken")).isInstanceOf(String.class);
        assertThat(registered.get("refreshToken")).isInstanceOf(String.class);
        assertThat(registered).containsKey("accessTokenExpiresAt");
        assertThat((Map<String, Object>) registered.get("account")).containsEntry("profileCompleted", true);
    }

    @Test
    void meWorksWithBearerToken() {
        Map<String, Object> registered = registerNewUser(newEmail());

        ResponseEntity<Map> response = get("/api/v1/me", (String) registered.get("accessToken"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("displayName", "Ada Lovelace");
    }

    @Test
    void meReturnsUnauthorizedWithoutToken() {
        ResponseEntity<Map> response = get("/api/v1/me", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("code", "UNAUTHORIZED");
    }

    @Test
    void existingUserCanLoginViaOtp() {
        String email = newEmail();
        registerNewUser(email);
        makeOtpCooldownOld();

        ResponseEntity<Map> start = startAuth(email);
        ResponseEntity<Map> verify = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", start.getBody().get("flowId"), "otp", DEBUG_OTP)
        );

        assertThat(verify.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verify.getBody()).containsEntry("nextStep", "AUTHENTICATED");
        assertThat(verify.getBody()).containsKey("accessToken");
        assertThat(verify.getBody()).containsKey("refreshToken");
    }

    @Test
    void refreshRotatesToken() {
        Map<String, Object> registered = registerNewUser(newEmail());
        String oldRefreshToken = (String) registered.get("refreshToken");

        ResponseEntity<Map> refresh = post(
                "/api/v1/auth/refresh",
                Map.of("refreshToken", oldRefreshToken)
        );
        ResponseEntity<Map> reuseOldToken = post(
                "/api/v1/auth/refresh",
                Map.of("refreshToken", oldRefreshToken)
        );

        assertThat(refresh.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(refresh.getBody()).containsEntry("nextStep", "AUTHENTICATED");
        assertThat(refresh.getBody().get("refreshToken")).isNotEqualTo(oldRefreshToken);
        assertThat(reuseOldToken.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void authStartIsLimitedByIdentifier() {
        String email = newEmail();
        ResponseEntity<Map> first = startAuth(email, "203.0.113.10");
        makeOtpCooldownOld();
        ResponseEntity<Map> second = startAuth(email, "203.0.113.11");
        makeOtpCooldownOld();
        ResponseEntity<Map> third = startAuth(email, "203.0.113.12");

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(third.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(third.getBody()).containsEntry("code", "RATE_LIMITED");
        assertThat(third.getBody()).containsKey("retryAfterSeconds");
        assertThat(third.getHeaders().getFirst("Retry-After")).isNotBlank();
    }

    @Test
    void authStartIsLimitedByIp() {
        String ip = "203.0.113.20";

        ResponseEntity<Map> first = startAuth(newEmail(), ip);
        ResponseEntity<Map> second = startAuth(newEmail(), ip);
        ResponseEntity<Map> third = startAuth(newEmail(), ip);

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(third.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(third.getBody()).containsEntry("code", "RATE_LIMITED");
    }

    @Test
    void resendCooldownBlocksRepeatedStartForSameIdentifier() {
        String email = newEmail();

        ResponseEntity<Map> first = startAuth(email, "203.0.113.30");
        ResponseEntity<Map> second = startAuth(email, "203.0.113.31");

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(second.getBody()).containsEntry("code", "RATE_LIMITED");
        assertThat(second.getBody()).containsKey("retryAfterSeconds");
    }

    @Test
    void wrongOtpLocksChallengeTemporarilyAndBlocksReuse() {
        ResponseEntity<Map> start = startAuth(newEmail(), "203.0.113.40");
        Object flowId = start.getBody().get("flowId");

        ResponseEntity<Map> firstWrong = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", flowId, "otp", "222222"),
                null,
                "203.0.113.40"
        );
        ResponseEntity<Map> secondWrong = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", flowId, "otp", "333333"),
                null,
                "203.0.113.40"
        );
        ResponseEntity<Map> blocked = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", flowId, "otp", DEBUG_OTP),
                null,
                "203.0.113.40"
        );

        assertThat(firstWrong.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(secondWrong.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(blocked.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(blocked.getBody()).containsEntry("code", "RATE_LIMITED");
    }

    @Test
    void usedOtpCannotBeReused() {
        ResponseEntity<Map> start = startAuth(newEmail(), "203.0.113.50");
        Object flowId = start.getBody().get("flowId");

        ResponseEntity<Map> firstUse = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", flowId, "otp", DEBUG_OTP),
                null,
                "203.0.113.50"
        );
        ResponseEntity<Map> secondUse = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", flowId, "otp", DEBUG_OTP),
                null,
                "203.0.113.50"
        );

        assertThat(firstUse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondUse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void expiredOtpCannotBeUsed() {
        ResponseEntity<Map> start = startAuth(newEmail(), "203.0.113.60");
        expireOtpFlows();

        ResponseEntity<Map> verify = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", start.getBody().get("flowId"), "otp", DEBUG_OTP),
                null,
                "203.0.113.60"
        );

        assertThat(verify.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void invalidRefreshTokenReturnsUnauthorized() {
        ResponseEntity<Map> response = post(
                "/api/v1/auth/refresh",
                Map.of("refreshToken", "not-a-valid-refresh-token")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void reusedRotatedRefreshTokenRevokesSession() {
        Map<String, Object> registered = registerNewUser(newEmail());
        String accessToken = (String) registered.get("accessToken");
        String oldRefreshToken = (String) registered.get("refreshToken");

        ResponseEntity<Map> refresh = post("/api/v1/auth/refresh", Map.of("refreshToken", oldRefreshToken));
        ResponseEntity<Map> reuseOldToken = post("/api/v1/auth/refresh", Map.of("refreshToken", oldRefreshToken));
        ResponseEntity<Map> me = get("/api/v1/me", accessToken);

        assertThat(refresh.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reuseOldToken.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void logoutRevokesSession() {
        Map<String, Object> registered = registerNewUser(newEmail());
        String accessToken = (String) registered.get("accessToken");

        ResponseEntity<Map> logout = post("/api/v1/auth/logout", Map.of(), accessToken);
        ResponseEntity<Map> me = get("/api/v1/me", accessToken);

        assertThat(logout.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void debugOtpOnlyWorksInLocalOrTestProfiles() {
        AuthProperties properties = new AuthProperties();
        properties.getJwt().setSecret("test-identity-jwt-secret-change-me");
        properties.getDebug().setEnabled(true);
        properties.getDebug().setFixedOtp(DEBUG_OTP);

        assertThatThrownBy(() -> AuthConfiguration.validate(properties, new String[] {"prod"}))
                .isInstanceOf(TechnicalException.class)
                .hasMessageContaining("Debug OTP can only be enabled");
    }

    @Test
    void seedUsersEnabledOutsideLocalOrTestFailsValidation() {
        AuthProperties properties = new AuthProperties();
        properties.getJwt().setSecret("test-identity-jwt-secret-change-me");
        properties.getDebug().setSeedTestUsers(true);

        assertThatThrownBy(() -> AuthConfiguration.validate(properties, new String[] {"prod"}))
                .isInstanceOf(TechnicalException.class)
                .hasMessageContaining("Debug seed users can only be enabled");
    }

    @Test
    void debugOtpIsNotReturnedWhenDebugIsDisabled() {
        AuthProperties properties = new AuthProperties();

        assertThat(properties.getDebug().isEnabled()).isFalse();
        assertThat(properties.getDebug().getFixedOtp()).isBlank();
    }

    @Test
    void smsProviderIsNotActiveByDefaultAndRequiresNetgsmConfigurationWhenEnabled() {
        AuthProperties properties = new AuthProperties();
        properties.getJwt().setSecret("test-identity-jwt-secret-change-me");

        assertThat(properties.getSms().getProvider()).isEqualTo(SmsProviderEnum.NONE);

        properties.getSms().setProvider(SmsProviderEnum.NETGSM);
        assertThatThrownBy(() -> AuthConfiguration.validate(properties, new String[] {"local"}))
                .isInstanceOf(TechnicalException.class)
                .hasMessageContaining("NetGSM SMS configuration is incomplete");
    }

    @Test
    void socialProvidersAreDisabledByDefault() {
        AuthProperties properties = new AuthProperties();

        assertThat(properties.getSocial().getGoogle().isEnabled()).isFalse();
        assertThat(properties.getSocial().getApple().isEnabled()).isFalse();
    }

    @Test
    void seededEmailUserCanLogin() {
        localTestUserSeeder.seedIfEnabled();

        ResponseEntity<Map> start = startAuth(LocalTestUserSeeder.EMAIL_IDENTIFIER, "203.0.113.70");
        ResponseEntity<Map> verify = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", start.getBody().get("flowId"), "otp", DEBUG_OTP),
                null,
                "203.0.113.70"
        );

        assertThat(start.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verify.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verify.getBody()).containsEntry("nextStep", "AUTHENTICATED");
        assertThat((Map<String, Object>) verify.getBody().get("account")).containsEntry("displayName", "Test User");
    }

    @Test
    void seededPhoneUserCanLogin() {
        localTestUserSeeder.seedIfEnabled();

        ResponseEntity<Map> start = startAuth(LocalTestUserSeeder.PHONE_IDENTIFIER, "203.0.113.71");
        ResponseEntity<Map> verify = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", start.getBody().get("flowId"), "otp", DEBUG_OTP),
                null,
                "203.0.113.71"
        );

        assertThat(start.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verify.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verify.getBody()).containsEntry("nextStep", "AUTHENTICATED");
        assertThat((Map<String, Object>) verify.getBody().get("account"))
                .containsEntry("displayName", "Test Phone User");
    }

    @Test
    void seedUsersAreIdempotent() {
        localTestUserSeeder.seedIfEnabled();
        localTestUserSeeder.seedIfEnabled();

        Integer accountCount = jdbcTemplate.queryForObject(
                """
                        SELECT count(DISTINCT account_id)
                        FROM identity_identifier
                        WHERE normalized_identifier IN (?, ?)
                        """,
                Integer.class,
                LocalTestUserSeeder.EMAIL_IDENTIFIER,
                LocalTestUserSeeder.PHONE_IDENTIFIER
        );
        Integer identifierCount = jdbcTemplate.queryForObject(
                """
                        SELECT count(*)
                        FROM identity_identifier
                        WHERE normalized_identifier IN (?, ?)
                        """,
                Integer.class,
                LocalTestUserSeeder.EMAIL_IDENTIFIER,
                LocalTestUserSeeder.PHONE_IDENTIFIER
        );

        assertThat(accountCount).isEqualTo(2);
        assertThat(identifierCount).isEqualTo(2);
    }

    @Test
    void seededUsersHaveVerifiedIdentifiersAndRequiredConsents() {
        localTestUserSeeder.seedIfEnabled();

        Integer verifiedIdentifierCount = jdbcTemplate.queryForObject(
                """
                        SELECT count(*)
                        FROM identity_identifier
                        WHERE normalized_identifier IN (?, ?)
                          AND verified_at IS NOT NULL
                        """,
                Integer.class,
                LocalTestUserSeeder.EMAIL_IDENTIFIER,
                LocalTestUserSeeder.PHONE_IDENTIFIER
        );
        Integer requiredConsentCount = jdbcTemplate.queryForObject(
                """
                        SELECT count(*)
                        FROM consent_record cr
                        JOIN identity_identifier ii ON ii.account_id = cr.account_id
                        WHERE ii.normalized_identifier IN (?, ?)
                          AND cr.consent_category = 'REQUIRED_LEGAL'
                          AND cr.consent_type IN ('TERMS_OF_SERVICE', 'PERSONAL_DATA_PROTECTION_LAW')
                          AND cr.accepted = true
                        """,
                Integer.class,
                LocalTestUserSeeder.EMAIL_IDENTIFIER,
                LocalTestUserSeeder.PHONE_IDENTIFIER
        );

        assertThat(verifiedIdentifierCount).isEqualTo(2);
        assertThat(requiredConsentCount).isEqualTo(4);
    }

    @Test
    void missingRequiredConsentFailsRegistration() {
        Map<String, Object> verified = verifyNewUser(newEmail());
        Map<String, Object> request = registrationRequest((String) verified.get("registrationToken"));
        request.put("acceptedRequiredConsents", List.of("TERMS_OF_SERVICE"));

        ResponseEntity<Map> response = post("/api/v1/auth/register", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("code", "VALIDATION_FAILED");
    }

    @Test
    void requiredConsentsEndpointPublishesServerOwnedVersions() {
        ResponseEntity<Map> response = get("/api/v1/auth/required-consents", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> required = (List<Map<String, Object>>) response.getBody().get("required");
        assertThat(required).extracting(item -> item.get("type"))
                .containsExactlyInAnyOrder("TERMS_OF_SERVICE", "PERSONAL_DATA_PROTECTION_LAW");
        assertThat(required).allSatisfy(item -> {
            assertThat(item.get("version")).isNotNull();
            assertThat(item.get("url")).isNotNull();
        });
        Map<String, Object> marketing = (Map<String, Object>) response.getBody().get("marketing");
        assertThat(marketing).containsEntry("type", "MARKETING_COMMUNICATION");
        assertThat(marketing).containsKey("version");
    }

    @Test
    void adminInvitationRequiresAdminRole() {
        Map<String, Object> registered = registerNewUser(newEmail());

        ResponseEntity<Map> response = post(
                "/api/v1/admin/invitations",
                Map.of(),
                (String) registered.get("accessToken")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void adminInvitationCreatesAdminAccountAndCannotBeReused() {
        String adminToken = issueAdminInvitationAsSeededAdmin();
        Map<String, Object> verified = verifyNewUser(newEmail());
        Map<String, Object> request = registrationRequest((String) verified.get("registrationToken"));
        request.put("invitationToken", adminToken);

        ResponseEntity<Map> registerAdmin = post("/api/v1/auth/register-admin", request);
        ResponseEntity<Map> reuse = post("/api/v1/auth/register-admin", request);

        assertThat(registerAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reuse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtRoles((String) registerAdmin.getBody().get("accessToken")))
                .containsExactlyInAnyOrder("USER", "ADMIN");
    }

    private ResponseEntity<Map> startAuth(String email) {
        return post("/api/v1/auth/start", Map.of("identifier", email));
    }

    private ResponseEntity<Map> startAuth(String email, String clientIp) {
        return post("/api/v1/auth/start", Map.of("identifier", email), null, clientIp);
    }

    private Map<String, Object> verifyNewUser(String email) {
        ResponseEntity<Map> start = startAuth(email);
        ResponseEntity<Map> verify = post(
                "/api/v1/auth/verify-otp",
                Map.of("flowId", start.getBody().get("flowId"), "otp", DEBUG_OTP)
        );
        assertThat(verify.getStatusCode()).isEqualTo(HttpStatus.OK);
        return verify.getBody();
    }

    private Map<String, Object> registerNewUser(String email) {
        Map<String, Object> verified = verifyNewUser(email);
        ResponseEntity<Map> register = post(
                "/api/v1/auth/register",
                registrationRequest((String) verified.get("registrationToken"))
        );
        assertThat(register.getStatusCode()).isEqualTo(HttpStatus.OK);
        return register.getBody();
    }

    private Map<String, Object> registrationRequest(String registrationToken) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("registrationToken", registrationToken);
        request.put("displayName", "Ada Lovelace");
        request.put("firstName", "Ada");
        request.put("lastName", "Lovelace");
        request.put("acceptedRequiredConsents", List.of("TERMS_OF_SERVICE", "PERSONAL_DATA_PROTECTION_LAW"));
        request.put("marketingConsentAccepted", false);
        return request;
    }

    private String newEmail() {
        return "identity-" + UUID.randomUUID() + "@example.com";
    }

    private String issueAdminInvitationAsSeededAdmin() {
        Instant now = Instant.now();
        UUID adminId = UUID.randomUUID();
        accountRepository.save(new IdentityAccountJpaEntity(
                adminId,
                AccountStatusEnum.ACTIVE,
                Set.of(AccountRoleEnum.USER, AccountRoleEnum.ADMIN),
                "Ops Admin",
                "Ops",
                "Admin",
                true,
                now,
                now
        ));
        UUID sessionId = UUID.randomUUID();
        jdbcTemplate.update(
                """
                        INSERT INTO auth_session (
                            id, account_id, status, issued_at, expires_at,
                            user_agent, created_at, updated_at
                        ) VALUES (?, ?, 'ACTIVE', ?, ?, ?, ?, ?)
                        """,
                sessionId,
                adminId,
                java.sql.Timestamp.from(now),
                java.sql.Timestamp.from(now.plusSeconds(3600)),
                "test",
                java.sql.Timestamp.from(now),
                java.sql.Timestamp.from(now)
        );
        String accessToken = jwtAccessTokenService.issue(
                adminId,
                sessionId,
                Set.of(AccountRoleEnum.USER, AccountRoleEnum.ADMIN),
                now
        );
        ResponseEntity<Map> response = post(
                "/api/v1/admin/invitations",
                Map.of(),
                accessToken
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return (String) response.getBody().get("invitationToken");
    }

    private List<String> jwtRoles(String token) {
        try {
            String payload = token.split("\\.")[1];
            byte[] decoded = java.util.Base64.getUrlDecoder().decode(payload);
            Map<String, Object> claims = OBJECT_MAPPER.readValue(decoded, new TypeReference<>() {});
            return (List<String>) claims.get("roles");
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private ResponseEntity<Map> post(String path, Object body) {
        return post(path, body, null);
    }

    private ResponseEntity<Map> post(String path, Object body, String accessToken) {
        return post(path, body, accessToken, null);
    }

    private ResponseEntity<Map> post(String path, Object body, String accessToken, String clientIp) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl(path)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(body)));
            if (accessToken != null) {
                builder.header("Authorization", "Bearer " + accessToken);
            }
            if (clientIp != null) {
                builder.header("X-Forwarded-For", clientIp);
            }
            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            return toResponseEntity(response);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private ResponseEntity<Map> get(String path, String accessToken) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl(path))).GET();
            if (accessToken != null) {
                builder.header("Authorization", "Bearer " + accessToken);
            }
            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            return toResponseEntity(response);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private ResponseEntity<Map> toResponseEntity(HttpResponse<String> response) throws Exception {
        Map<String, Object> body = response.body() == null || response.body().isBlank()
                ? Map.of()
                : OBJECT_MAPPER.readValue(response.body(), new TypeReference<>() {
                });
        HttpStatus status = HttpStatus.valueOf(response.statusCode());
        if (status.is2xxSuccessful() && body.containsKey("success") && body.containsKey("data")) {
            Object data = body.get("data");
            body = data instanceof Map ? (Map<String, Object>) data : Map.of();
        }
        HttpHeaders headers = new HttpHeaders();
        response.headers().map().forEach(headers::put);
        return new ResponseEntity<>(body, headers, status);
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    private void makeOtpCooldownOld() {
        jdbcTemplate.execute("UPDATE auth_otp_challenge SET created_at = now() - interval '120 seconds'");
        try (var connection = redisConnectionFactory.getConnection()) {
            connection.keyCommands().keys("rl:auth_start_identifier:resend:*".getBytes())
                    .forEach(connection.keyCommands()::del);
        }
    }

    private void expireOtpFlows() {
        jdbcTemplate.execute("UPDATE auth_login_flow SET expires_at = now() - interval '1 second'");
        jdbcTemplate.execute("UPDATE auth_otp_challenge SET expires_at = now() - interval '1 second'");
    }

    static class FlywayInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Flyway.configure()
                    .dataSource(
                            IdentityTestcontainers.POSTGRES.getJdbcUrl(),
                            IdentityTestcontainers.POSTGRES.getUsername(),
                            IdentityTestcontainers.POSTGRES.getPassword()
                    )
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        }
    }
}
