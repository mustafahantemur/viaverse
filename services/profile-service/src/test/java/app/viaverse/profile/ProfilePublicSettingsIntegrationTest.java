package app.viaverse.profile;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.contracts.identity.account.IdentityAccountEventTypes;
import app.viaverse.contracts.trust.score.TrustEventTypes;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
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
@ContextConfiguration(initializers = ProfilePublicSettingsIntegrationTest.FlywayInitializer.class)
class ProfilePublicSettingsIntegrationTest {

    private static final String JWT_SECRET = "test-identity-jwt-secret-change-me";
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
    @Qualifier("trustScoreEventsConsumer")
    private Consumer<Message<Map<String, Object>>> trustConsumer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID ownerId;
    private UUID viewerId;
    private String ownerToken;
    private String viewerToken;

    @BeforeEach
    void setUp() throws Exception {
        jdbcTemplate.execute("DELETE FROM consumed_event");
        jdbcTemplate.execute("DELETE FROM profile_block");
        jdbcTemplate.execute("DELETE FROM profile_preference");
        jdbcTemplate.execute("DELETE FROM business_profile");
        jdbcTemplate.execute("DELETE FROM individual_provider_profile");
        jdbcTemplate.execute("DELETE FROM profile_capability");
        jdbcTemplate.execute("DELETE FROM profile_trust_snapshot");
        jdbcTemplate.execute("DELETE FROM profile");
        jdbcTemplate.execute("DELETE FROM outbox_event");

        ownerId = UUID.randomUUID();
        viewerId = UUID.randomUUID();
        consumer.accept(accountCreatedMessage(UUID.randomUUID(), ownerId, "Ada Lovelace"));
        consumer.accept(accountCreatedMessage(UUID.randomUUID(), viewerId, "Grace Hopper"));
        ownerToken = accessToken(ownerId);
        viewerToken = accessToken(viewerId);

        patch("/api/v1/me/profile", Map.of(
                "headline", "Mathematician",
                "bio", "Writes the first algorithm."
        ), ownerToken);
    }

    @Test
    void publicProfileRedactsLimitedAnonymousViewButShowsAuthenticatedViewer() throws Exception {
        Map<String, Object> anonymous = unwrap(get("/api/v1/profiles/" + ownerId, null).body());
        Map<String, Object> authenticated = unwrap(get("/api/v1/profiles/" + ownerId, viewerToken).body());

        assertThat(anonymous).containsEntry("displayName", "Ada Lovelace");
        assertThat(anonymous.get("headline")).isNull();
        assertThat(anonymous.get("bio")).isNull();
        assertThat(authenticated).containsEntry("headline", "Mathematician");
        assertThat(authenticated).containsEntry("bio", "Writes the first algorithm.");
    }

    @Test
    void blockedViewerGetsPrivateLikePublicProfile() throws Exception {
        patch("/api/v1/me/profile", Map.of("publicVisibility", "PUBLIC"), ownerToken);
        post("/api/v1/me/blocks", Map.of(
                "blockedAccountId", viewerId.toString(),
                "reason", "spam"
        ), ownerToken);

        Map<String, Object> blockedView = unwrap(get("/api/v1/profiles/" + ownerId, viewerToken).body());

        assertThat(blockedView).containsEntry("displayName", "Ada Lovelace");
        assertThat(blockedView.get("headline")).isNull();
        assertThat(blockedView.get("bio")).isNull();
        assertThat(blockedView).containsEntry("trustBadge", "NONE");
    }

    @Test
    void preferencesRoundTripAndBlocksEmitLifecycleEvents() throws Exception {
        HttpResponse<String> putPreference = put(
                "/api/v1/me/preferences/ui.theme",
                Map.of("mode", "dark"),
                ownerToken
        );
        Map<String, Object> preferences = unwrap(get("/api/v1/me/preferences", ownerToken).body());

        assertThat(putPreference.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat((Map<String, Object>) preferences.get("ui.theme")).containsEntry("mode", "dark");

        post("/api/v1/me/blocks", Map.of(
                "blockedAccountId", viewerId.toString(),
                "reason", "spam"
        ), ownerToken);
        List<Map<String, Object>> blocks = unwrapList(get("/api/v1/me/blocks", ownerToken).body());
        delete("/api/v1/me/blocks/" + viewerId, ownerToken);

        assertThat(blocks).hasSize(1);
        assertThat(blocks.getFirst()).containsEntry("reason", "spam");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM outbox_event WHERE event_type = ?",
                Integer.class,
                "profile.ProfileBlocked.v1"
        )).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM outbox_event WHERE event_type = ?",
                Integer.class,
                "profile.ProfileUnblocked.v1"
        )).isEqualTo(1);
    }

    @Test
    void publicProfileSurfacesTrustBadgeWhenViewerMaySeeBadges() throws Exception {
        trustConsumer.accept(trustScoreUpdatedMessage(UUID.randomUUID(), ownerId, 240, "VERIFIED_HUMAN"));

        Map<String, Object> anonymous = unwrap(get("/api/v1/profiles/" + ownerId, null).body());
        Map<String, Object> authenticated = unwrap(get("/api/v1/profiles/" + ownerId, viewerToken).body());

        assertThat(anonymous).containsEntry("trustBadge", "VERIFIED_HUMAN");
        assertThat(authenticated).containsEntry("trustBadge", "VERIFIED_HUMAN");
    }

    private Message<Map<String, Object>> accountCreatedMessage(UUID eventId, UUID accountId, String displayName) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", eventId.toString());
        payload.put("occurredAt", Instant.parse("2026-05-18T08:00:00Z").toString());
        payload.put("version", "v1");
        payload.put("accountId", accountId.toString());
        payload.put("displayName", displayName);
        payload.put("firstName", displayName.split(" ")[0]);
        payload.put("lastName", displayName.split(" ")[1]);
        return MessageBuilder.withPayload(payload)
                .setHeader("eventType", IdentityAccountEventTypes.ACCOUNT_CREATED_V1)
                .build();
    }

    private Message<Map<String, Object>> trustScoreUpdatedMessage(
            UUID eventId,
            UUID accountId,
            int score,
            String level
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", eventId.toString());
        payload.put("occurredAt", Instant.parse("2026-05-18T09:00:00Z").toString());
        payload.put("version", "v1");
        payload.put("accountId", accountId.toString());
        payload.put("score", score);
        payload.put("level", level);
        payload.put("badge", level);
        return MessageBuilder.withPayload(payload)
                .setHeader("eventType", TrustEventTypes.TRUST_SCORE_UPDATED_V1)
                .build();
    }

    private String accessToken(UUID accountId) {
        SecretKey key = new SecretKeySpec(JWT_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        Instant now = Instant.now();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder()
                        .issuer("viaverse-identity")
                        .subject(accountId.toString())
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

    private HttpResponse<String> put(String path, Object body, String bearerToken) throws Exception {
        return sendWithBody("PUT", path, body, bearerToken);
    }

    private HttpResponse<String> post(String path, Object body, String bearerToken) throws Exception {
        return sendWithBody("POST", path, body, bearerToken);
    }

    private HttpResponse<String> patch(String path, Object body, String bearerToken) throws Exception {
        return sendWithBody("PATCH", path, body, bearerToken);
    }

    private HttpResponse<String> delete(String path, String bearerToken) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Authorization", "Bearer " + bearerToken)
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private HttpResponse<String> sendWithBody(String method, String path, Object body, String bearerToken)
            throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + bearerToken)
                        .method(method, HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
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
    private List<Map<String, Object>> unwrapList(String raw) throws Exception {
        Map<String, Object> envelope = JSON.readValue(raw, new TypeReference<>() {});
        Object data = envelope.get("data");
        return data instanceof List<?> list ? (List<Map<String, Object>>) list : List.of();
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
