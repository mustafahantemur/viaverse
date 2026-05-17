package app.viaverse.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.infrastructure.adapter.out.seed.LocalTestUserSeeder;
import app.viaverse.identity.auth.infrastructure.security.AccountSecretCipher;
import app.viaverse.identity.auth.infrastructure.security.TotpService;
import app.viaverse.identity.config.AuthConfiguration;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.auth.domain.enums.SmsProviderEnum;
import app.viaverse.identity.support.IdentityTestcontainers;
import app.viaverse.identity.support.RecordingOtpDeliveryAdapter;
import app.viaverse.shared.kernel.error.TechnicalException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.binary.Base32;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Full-stack integration test exercising the password + 2FA auth flow.
 * Uses Testcontainers Postgres / Valkey / Kafka and replaces the OTP
 * delivery layer with a {@link RecordingOtpDeliveryAdapter} so tests can
 * pull the plaintext code without depending on Mailpit / NetGSM.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "viaverse.auth.otp.max-attempts=5",
                "viaverse.auth.rate-limit.auth-start.identifier-max-attempts=10",
                "viaverse.auth.rate-limit.auth-start.ip-max-attempts=20",
                "viaverse.auth.rate-limit.otp-verify.flow-max-attempts=5",
                "viaverse.auth.rate-limit.otp-verify.ip-max-attempts=20",
                "viaverse.auth.rate-limit.password-login.identifier-max-attempts=5",
                "viaverse.auth.rate-limit.password-login.ip-max-attempts=20",
                "viaverse.auth.rate-limit.resend.cooldown-seconds=0"
        }
)
@ActiveProfiles("test")
@ContextConfiguration(initializers = IdentityAuthIntegrationTest.FlywayInitializer.class)
@Import(IdentityAuthIntegrationTest.TestOtpConfiguration.class)
class IdentityAuthIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TEST_PASSWORD = "Pa55word!Pass";

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

    @TestConfiguration
    static class TestOtpConfiguration {
        @Bean
        @Primary
        @Order(0)
        RecordingOtpDeliveryAdapter recordingOtpDeliveryAdapter() {
            return new RecordingOtpDeliveryAdapter();
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LocalTestUserSeeder localTestUserSeeder;

    @Autowired
    private RecordingOtpDeliveryAdapter otpRecorder;

    @Autowired
    private TotpService totpService;

    @Autowired
    private AccountSecretCipher accountSecretCipher;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void cleanState() {
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
        jdbcTemplate.execute("DELETE FROM account_backup_code");
        jdbcTemplate.execute("DELETE FROM identity_account_role");
        jdbcTemplate.execute("DELETE FROM identity_account");
        jdbcTemplate.execute("DELETE FROM audit_log");
        otpRecorder.clear();
    }

    // ---------- /auth/start ----------

    @Test
    void startWithUnknownIdentifierReturnsOtpRequiredAndIssuesOtp() {
        String email = newEmail();
        ResponseEntity<Map> response = startAuth(email);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("identifierType", "EMAIL");
        assertThat(response.getBody()).containsEntry("nextStep", "OTP_REQUIRED");
        assertThat(response.getBody()).containsKey("flowId");
        assertThat(otpRecorder.latestFor(IdentifierTypeEnum.EMAIL, email)).hasSize(6);
    }

    @Test
    void startWithKnownIdentifierReturnsPasswordRequiredAndDoesNotIssueOtp() {
        registerWithPassword(newEmail());
        String existing = lastSeededEmail();

        ResponseEntity<Map> response = startAuth(existing);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("nextStep", "PASSWORD_REQUIRED");
        assertThat(response.getBody().get("flowId")).isNull();
    }

    // ---------- /auth/verify-otp + /auth/register ----------

    @Test
    void registrationFlowEndToEndWithPassword() {
        String email = newEmail();
        ResponseEntity<Map> start = startAuth(email);
        String otp = otpRecorder.latestFor(IdentifierTypeEnum.EMAIL, email);

        ResponseEntity<Map> verify = post("/api/v1/auth/verify-otp",
                Map.of("flowId", start.getBody().get("flowId"), "otp", otp));
        assertThat(verify.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verify.getBody()).containsEntry("nextStep", "REGISTRATION_REQUIRED");
        String registrationToken = (String) verify.getBody().get("registrationToken");

        ResponseEntity<Map> register = post("/api/v1/auth/register",
                registrationRequest(registrationToken, TEST_PASSWORD));
        assertThat(register.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(register.getBody()).containsEntry("nextStep", "AUTHENTICATED");
        assertThat(register.getBody().get("accessToken")).isInstanceOf(String.class);
        assertThat(register.getBody().get("refreshToken")).isInstanceOf(String.class);
    }

    @Test
    void registerWithWeakPasswordFails() {
        String email = newEmail();
        String registrationToken = obtainRegistrationToken(email);

        Map<String, Object> req = registrationRequest(registrationToken, "weak");
        ResponseEntity<Map> response = post("/api/v1/auth/register", req);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("identityCode", "AUTH_PASSWORD_POLICY_VIOLATION");
    }

    @Test
    void registerWithMissingPasswordFails() {
        String email = newEmail();
        String registrationToken = obtainRegistrationToken(email);

        Map<String, Object> req = registrationRequest(registrationToken, null);
        ResponseEntity<Map> response = post("/api/v1/auth/register", req);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("identityCode", "AUTH_PASSWORD_POLICY_VIOLATION");
    }

    // ---------- /auth/password-login ----------

    @Test
    void passwordLoginSucceedsForRegisteredUser() {
        String email = newEmail();
        registerWithPassword(email);

        ResponseEntity<Map> response = post("/api/v1/auth/password-login",
                Map.of("identifier", email, "password", TEST_PASSWORD));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("nextStep", "AUTHENTICATED");
        assertThat(response.getBody().get("accessToken")).isInstanceOf(String.class);
    }

    @Test
    void passwordLoginFailsWithWrongPassword() {
        String email = newEmail();
        registerWithPassword(email);

        ResponseEntity<Map> response = post("/api/v1/auth/password-login",
                Map.of("identifier", email, "password", "Wrong!Password9"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("identityCode", "AUTH_INVALID_CREDENTIALS");
    }

    @Test
    void passwordLoginFailsWithUnknownIdentifier() {
        ResponseEntity<Map> response = post("/api/v1/auth/password-login",
                Map.of("identifier", newEmail(), "password", TEST_PASSWORD));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("identityCode", "AUTH_INVALID_CREDENTIALS");
    }

    // ---------- /me ----------

    @Test
    void meWorksWithAccessTokenFromPasswordLogin() {
        String email = newEmail();
        Map<String, Object> reg = registerWithPassword(email);

        ResponseEntity<Map> response = get("/api/v1/me", (String) reg.get("accessToken"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("displayName", "Ada Lovelace");
    }

    @Test
    void meReturnsUnauthorizedWithoutToken() {
        ResponseEntity<Map> response = get("/api/v1/me", null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ---------- 2FA enrollment + login ----------

    @Test
    void twoFactorEnrollmentAndLogin() throws Exception {
        String email = newEmail();
        Map<String, Object> reg = registerWithPassword(email);
        String accessToken = (String) reg.get("accessToken");

        ResponseEntity<Map> enroll = post("/api/v1/me/2fa/enroll", Map.of(), accessToken);
        assertThat(enroll.getStatusCode()).isEqualTo(HttpStatus.OK);
        String secretBase32 = (String) enroll.getBody().get("secretBase32");
        UUID flowId = UUID.fromString((String) enroll.getBody().get("flowId"));
        assertThat(secretBase32).isNotBlank();

        String enrollmentOtp = otpRecorder.latestFor(IdentifierTypeEnum.EMAIL, email);
        byte[] secret = new Base32().decode(padBase32(secretBase32));
        String firstTotp = computeTotp(secret);

        ResponseEntity<Map> confirm = post(
                "/api/v1/me/2fa/confirm",
                Map.of("flowId", flowId.toString(), "otp", enrollmentOtp, "totpCode", firstTotp),
                accessToken
        );
        assertThat(confirm.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> backupCodes = (List<String>) confirm.getBody().get("backupCodes");
        assertThat(backupCodes).hasSize(10);

        // Now password-login should require TOTP
        ResponseEntity<Map> pwLogin = post("/api/v1/auth/password-login",
                Map.of("identifier", email, "password", TEST_PASSWORD));
        assertThat(pwLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pwLogin.getBody()).containsEntry("nextStep", "TOTP_REQUIRED");
        String partialAuthToken = (String) pwLogin.getBody().get("partialAuthToken");

        ResponseEntity<Map> totpVerify = post("/api/v1/auth/verify-totp",
                Map.of("partialAuthToken", partialAuthToken, "totpCode", computeTotp(secret)));
        assertThat(totpVerify.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(totpVerify.getBody()).containsEntry("nextStep", "AUTHENTICATED");
        assertThat(totpVerify.getBody().get("accessToken")).isInstanceOf(String.class);
    }

    @Test
    void verifyTotpRejectsInvalidCode() {
        String email = newEmail();
        Map<String, Object> reg = registerWithPassword(email);
        enrollTwoFactor(email, (String) reg.get("accessToken"));

        Map<String, Object> pw = post("/api/v1/auth/password-login",
                Map.of("identifier", email, "password", TEST_PASSWORD)).getBody();

        ResponseEntity<Map> bad = post("/api/v1/auth/verify-totp",
                Map.of("partialAuthToken", pw.get("partialAuthToken"), "totpCode", "000000"));
        assertThat(bad.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(bad.getBody()).containsEntry("identityCode", "AUTH_TOTP_INVALID");
    }

    // ---------- /auth/refresh ----------

    @Test
    void refreshRotatesToken() {
        Map<String, Object> reg = registerWithPassword(newEmail());
        String oldRefresh = (String) reg.get("refreshToken");

        ResponseEntity<Map> refresh = post("/api/v1/auth/refresh", Map.of("refreshToken", oldRefresh));
        ResponseEntity<Map> reuse = post("/api/v1/auth/refresh", Map.of("refreshToken", oldRefresh));

        assertThat(refresh.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(refresh.getBody().get("refreshToken")).isNotEqualTo(oldRefresh);
        assertThat(reuse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ---------- /auth/logout ----------

    @Test
    void logoutRevokesSession() {
        Map<String, Object> reg = registerWithPassword(newEmail());
        String accessToken = (String) reg.get("accessToken");

        ResponseEntity<Map> logout = post("/api/v1/auth/logout", Map.of(), accessToken);
        ResponseEntity<Map> me = get("/api/v1/me", accessToken);

        assertThat(logout.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ---------- Consent ----------

    @Test
    void requiredConsentsEndpointPublishesServerOwnedVersions() {
        ResponseEntity<Map> response = get("/api/v1/auth/required-consents", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> required = (List<Map<String, Object>>) response.getBody().get("required");
        assertThat(required).extracting(item -> item.get("type"))
                .containsExactlyInAnyOrder("TERMS_OF_SERVICE", "PERSONAL_DATA_PROTECTION_LAW");
    }

    @Test
    void missingRequiredConsentFailsRegistration() {
        String email = newEmail();
        String registrationToken = obtainRegistrationToken(email);
        Map<String, Object> req = registrationRequest(registrationToken, TEST_PASSWORD);
        req.put("acceptedRequiredConsents", List.of("TERMS_OF_SERVICE"));

        ResponseEntity<Map> response = post("/api/v1/auth/register", req);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("identityCode", "AUTH_REQUIRED_CONSENTS_MISSING");
    }

    // ---------- Config validation ----------

    @Test
    void debugFlagRejectedOutsideLocalOrTestProfile() {
        AuthProperties properties = new AuthProperties();
        properties.getJwt().setSecret("test-identity-jwt-secret-change-me");
        properties.getDebug().setEnabled(true);

        assertThatThrownBy(() -> AuthConfiguration.validate(properties, new String[] {"prod"}))
                .isInstanceOf(TechnicalException.class)
                .hasMessageContaining("Debug OTP");
    }

    @Test
    void smsProviderRequiresNetgsmConfigurationWhenEnabled() {
        AuthProperties properties = new AuthProperties();
        properties.getJwt().setSecret("test-identity-jwt-secret-change-me");
        properties.getSms().setProvider(SmsProviderEnum.NETGSM);

        assertThatThrownBy(() -> AuthConfiguration.validate(properties, new String[] {"local"}))
                .isInstanceOf(TechnicalException.class)
                .hasMessageContaining("NetGSM SMS configuration is incomplete");
    }

    // ---------- Seeded users ----------

    @Test
    void seededUserCanLoginWithSeedPassword() {
        localTestUserSeeder.seedIfEnabled();

        ResponseEntity<Map> login = post("/api/v1/auth/password-login",
                Map.of(
                        "identifier", LocalTestUserSeeder.EMAIL_IDENTIFIER,
                        "password", LocalTestUserSeeder.SEED_PASSWORD
                )
        );
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).containsEntry("nextStep", "AUTHENTICATED");
    }

    // ---------- helpers ----------

    private String lastSeededEmail;

    private String newEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }

    private String lastSeededEmail() {
        return lastSeededEmail;
    }

    private String obtainRegistrationToken(String email) {
        ResponseEntity<Map> start = startAuth(email);
        String otp = otpRecorder.latestFor(IdentifierTypeEnum.EMAIL, email);
        ResponseEntity<Map> verify = post("/api/v1/auth/verify-otp",
                Map.of("flowId", start.getBody().get("flowId"), "otp", otp));
        return (String) verify.getBody().get("registrationToken");
    }

    private Map<String, Object> registerWithPassword(String email) {
        this.lastSeededEmail = email;
        String registrationToken = obtainRegistrationToken(email);
        ResponseEntity<Map> register = post("/api/v1/auth/register",
                registrationRequest(registrationToken, TEST_PASSWORD));
        assertThat(register.getStatusCode()).isEqualTo(HttpStatus.OK);
        return register.getBody();
    }

    private void enrollTwoFactor(String email, String accessToken) {
        ResponseEntity<Map> enroll = post("/api/v1/me/2fa/enroll", Map.of(), accessToken);
        String secretBase32 = (String) enroll.getBody().get("secretBase32");
        UUID flowId = UUID.fromString((String) enroll.getBody().get("flowId"));
        String otp = otpRecorder.latestFor(IdentifierTypeEnum.EMAIL, email);
        byte[] secret = new Base32().decode(padBase32(secretBase32));
        ResponseEntity<Map> confirm = post(
                "/api/v1/me/2fa/confirm",
                Map.of("flowId", flowId.toString(), "otp", otp, "totpCode", computeTotp(secret)),
                accessToken
        );
        assertThat(confirm.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private Map<String, Object> registrationRequest(String registrationToken, String password) {
        Map<String, Object> req = new LinkedHashMap<>();
        req.put("registrationToken", registrationToken);
        req.put("displayName", "Ada Lovelace");
        req.put("firstName", "Ada");
        req.put("lastName", "Lovelace");
        if (password != null) {
            req.put("password", password);
        }
        req.put("acceptedRequiredConsents", List.of("TERMS_OF_SERVICE", "PERSONAL_DATA_PROTECTION_LAW"));
        req.put("marketingConsentAccepted", false);
        return req;
    }

    private ResponseEntity<Map> startAuth(String identifier) {
        return post("/api/v1/auth/start", Map.of("identifier", identifier));
    }

    private String computeTotp(byte[] secret) {
        // Use the same TotpService the server uses to keep math identical.
        return totpProvisioningCode(secret, Instant.now());
    }

    private String totpProvisioningCode(byte[] secret, Instant now) {
        // Inline simplified RFC 6238 to avoid a circular dependency on TotpService
        // for the *generation* side; we still use the server's TotpService for
        // verification. Computes the current step's 6-digit code.
        try {
            long step = now.getEpochSecond() / 30L;
            byte[] counter = java.nio.ByteBuffer.allocate(Long.BYTES).putLong(step).array();
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret, "HmacSHA1"));
            byte[] hmac = mac.doFinal(counter);
            int offset = hmac[hmac.length - 1] & 0x0F;
            int binary = ((hmac[offset] & 0x7F) << 24)
                    | ((hmac[offset + 1] & 0xFF) << 16)
                    | ((hmac[offset + 2] & 0xFF) << 8)
                    | (hmac[offset + 3] & 0xFF);
            return String.format("%06d", binary % 1_000_000);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String padBase32(String raw) {
        int mod = raw.length() % 8;
        if (mod == 0) return raw;
        return raw + "========".substring(0, 8 - mod);
    }

    // ---------- HTTP ----------

    private ResponseEntity<Map> post(String path, Object body) {
        return post(path, body, null);
    }

    private ResponseEntity<Map> post(String path, Object body, String accessToken) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(body)));
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

    private ResponseEntity<Map> get(String path, String accessToken) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET();
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
                : OBJECT_MAPPER.readValue(response.body(), new TypeReference<>() {});
        HttpStatus status = HttpStatus.valueOf(response.statusCode());
        if (status.is2xxSuccessful() && body.containsKey("success") && body.containsKey("data")) {
            Object data = body.get("data");
            body = data instanceof Map ? (Map<String, Object>) data : Map.of();
        }
        HttpHeaders headers = new HttpHeaders();
        response.headers().map().forEach(headers::put);
        return new ResponseEntity<>(body, headers, status);
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
