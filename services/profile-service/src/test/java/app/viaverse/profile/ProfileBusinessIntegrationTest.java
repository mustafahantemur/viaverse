package app.viaverse.profile;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.contracts.identity.account.IdentityAccountEventTypes;
import app.viaverse.profile.profile.application.port.out.IdentityProviderGateway;
import app.viaverse.profile.support.ProfileTestcontainers;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = ProfileBusinessIntegrationTest.FlywayInitializer.class)
@Import(ProfileBusinessIntegrationTest.TestIdentityGatewayConfiguration.class)
class ProfileBusinessIntegrationTest {

    private static final String JWT_SECRET = "test-identity-jwt-secret-change-me";
    private static final String INTERNAL_TOKEN = "local-dev-internal-token-change-me";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", ProfileTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", ProfileTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", ProfileTestcontainers.POSTGRES::getPassword);
        registry.add("spring.cloud.stream.kafka.binder.brokers", ProfileTestcontainers.KAFKA::getBootstrapServers);
        registry.add("viaverse.profile.security.jwt.secret", () -> JWT_SECRET);
    }

    @LocalServerPort
    private int port;

    @Autowired
    @Qualifier("identityAccountEventsConsumer")
    private Consumer<Message<Map<String, Object>>> consumer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID accountId;
    private String accessToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM consumed_event");
        jdbcTemplate.execute("DELETE FROM profile_block");
        jdbcTemplate.execute("DELETE FROM profile_preference");
        jdbcTemplate.execute("DELETE FROM business_profile");
        jdbcTemplate.execute("DELETE FROM individual_provider_profile");
        jdbcTemplate.execute("DELETE FROM profile_capability");
        jdbcTemplate.execute("DELETE FROM profile");
        jdbcTemplate.execute("DELETE FROM outbox_event");
        accountId = UUID.randomUUID();
        consumer.accept(accountCreatedMessage(UUID.randomUUID(), accountId));
        accessToken = accessToken(accountId);
    }

    @Test
    void businessDraftCanBeSubmittedApprovedAndExposedPublicly() throws Exception {
        assertThat(post("/api/v1/me/capabilities/business/start", Map.of(), accessToken).statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(patch("/api/v1/me/business/draft", completeDraft(), accessToken).statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(post(
                "/api/v1/me/capabilities/business/submit",
                Map.of("acceptedBusinessTermsVersion", "v1"),
                accessToken
        ).statusCode()).isEqualTo(HttpStatus.OK.value());

        Map<String, Object> submissions = unwrapListEnvelope(getInternal(
                "/api/v1/internal/business/submissions"
        ).body()).getFirst();
        assertThat(submissions).containsEntry("tradeName", "Ada Pharmacy");

        assertThat(postInternal("/api/v1/internal/business/" + accountId + "/approve", Map.of()).statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(patch(
                "/api/v1/me/profile",
                Map.of("publicVisibility", "PUBLIC"),
                accessToken
        ).statusCode()).isEqualTo(HttpStatus.OK.value());

        Map<String, Object> publicProfile = unwrap(get("/api/v1/profiles/" + accountId, null).body());
        Map<String, Object> publicBusiness = (Map<String, Object>) publicProfile.get("businessProfile");

        assertThat(publicProfile.get("capabilities")).asList().contains("CUSTOMER", "BUSINESS");
        assertThat(publicBusiness).containsEntry("tradeName", "Ada Pharmacy");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT verification_status FROM business_profile WHERE account_id = ?",
                String.class,
                accountId
        )).isEqualTo("APPROVED");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT active_mode FROM profile WHERE account_id = ?",
                String.class,
                accountId
        )).isEqualTo("BUSINESS");
    }

    @Test
    void incompleteBusinessDraftCannotBeSubmitted() throws Exception {
        post("/api/v1/me/capabilities/business/start", Map.of(), accessToken);
        HttpResponse<String> response = post(
                "/api/v1/me/capabilities/business/submit",
                Map.of("acceptedBusinessTermsVersion", "v1"),
                accessToken
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(jdbcTemplate.queryForObject(
                "SELECT verification_status FROM business_profile WHERE account_id = ?",
                String.class,
                accountId
        )).isEqualTo("DRAFT");
    }

    private Map<String, Object> completeDraft() {
        return Map.ofEntries(
                Map.entry("legalName", "Ada Pharmacy Ltd."),
                Map.entry("tradeName", "Ada Pharmacy"),
                Map.entry("sector", "PHARMACY"),
                Map.entry("taxId", "1234567890"),
                Map.entry("addressLine", "Analytical Engine Street 1"),
                Map.entry("district", "Kadikoy"),
                Map.entry("city", "Istanbul"),
                Map.entry("country", "TR"),
                Map.entry("phone", "+905551112233"),
                Map.entry("emailPublic", "hello@adapharmacy.example"),
                Map.entry("openingHoursJson", "{\"mon\":\"09:00-18:00\"}")
        );
    }

    private Message<Map<String, Object>> accountCreatedMessage(UUID eventId, UUID currentAccountId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", eventId.toString());
        payload.put("occurredAt", Instant.parse("2026-05-18T08:00:00Z").toString());
        payload.put("version", "v1");
        payload.put("accountId", currentAccountId.toString());
        payload.put("displayName", "Ada Lovelace");
        payload.put("firstName", "Ada");
        payload.put("lastName", "Lovelace");
        return MessageBuilder.withPayload(payload)
                .setHeader("eventType", IdentityAccountEventTypes.ACCOUNT_CREATED_V1)
                .build();
    }

    private String accessToken(UUID currentAccountId) {
        SecretKey key = new SecretKeySpec(JWT_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        Instant now = Instant.now();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder()
                        .issuer("viaverse-identity")
                        .subject(currentAccountId.toString())
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(900))
                        .claim("sid", UUID.randomUUID().toString())
                        .claim("roles", List.of("USER"))
                        .build()
        )).getTokenValue();
    }

    private HttpResponse<String> get(String path, String bearerToken) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET();
        if (bearerToken != null) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }
        return HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getInternal(String path) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("X-Internal-Token", INTERNAL_TOKEN)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private HttpResponse<String> post(String path, Object body, String bearerToken) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + bearerToken)
                        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private HttpResponse<String> postInternal(String path, Object body) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Content-Type", "application/json")
                        .header("X-Internal-Token", INTERNAL_TOKEN)
                        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private HttpResponse<String> patch(String path, Object body, String bearerToken) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + bearerToken)
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> unwrap(String raw) throws Exception {
        Map<String, Object> envelope = JSON.readValue(raw, new TypeReference<>() {});
        Object data = envelope.get("data");
        return data instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> unwrapListEnvelope(String raw) throws Exception {
        Map<String, Object> envelope = JSON.readValue(raw, new TypeReference<>() {});
        Object data = envelope.get("data");
        return data instanceof List<?> list ? (List<Map<String, Object>>) list : List.of();
    }

    @TestConfiguration
    static class TestIdentityGatewayConfiguration {
        @Bean
        @Primary
        IdentityProviderGateway identityProviderGateway() {
            return new IdentityProviderGateway() {
                @Override
                public ProviderEnablementFacts getProviderEnablementFacts(UUID accountId) {
                    return new ProviderEnablementFacts(true, true, "v1");
                }

                @Override
                public void acceptProviderTerms(UUID accountId, String version) {
                }

                @Override
                public BusinessEnablementFacts getBusinessEnablementFacts(UUID accountId) {
                    return new BusinessEnablementFacts(true, true, "v1");
                }

                @Override
                public void acceptBusinessTerms(UUID accountId, String version) {
                }
            };
        }
    }

    static class FlywayInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Flyway.configure()
                    .dataSource(
                            ProfileTestcontainers.POSTGRES.getJdbcUrl(),
                            ProfileTestcontainers.POSTGRES.getUsername(),
                            ProfileTestcontainers.POSTGRES.getPassword()
                    )
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        }
    }
}
