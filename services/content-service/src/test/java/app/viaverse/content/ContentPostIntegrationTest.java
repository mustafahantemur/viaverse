package app.viaverse.content;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.content.support.ContentTestcontainers;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
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
@ContextConfiguration(initializers = ContentPostIntegrationTest.FlywayInitializer.class)
class ContentPostIntegrationTest {

    private static final String JWT_SECRET = "test-identity-jwt-secret-change-me";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", ContentTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", ContentTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", ContentTestcontainers.POSTGRES::getPassword);
        registry.add("spring.cloud.stream.kafka.binder.brokers", ContentTestcontainers.KAFKA::getBootstrapServers);
        registry.add("viaverse.content.security.jwt.secret", () -> JWT_SECRET);
    }

    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private UUID authorId;
    private String token;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM content_interaction");
        jdbcTemplate.execute("DELETE FROM content_post_media");
        jdbcTemplate.execute("DELETE FROM content_post");
        jdbcTemplate.execute("DELETE FROM outbox_event");
        authorId = UUID.randomUUID();
        token = accessToken(authorId);
    }

    @Test
    void userCanPublishAndReadLocalOrganicContent() throws Exception {
        Map<String, Object> created = unwrap(post("/api/v1/posts", Map.of(
                "authorMode", "BUSINESS",
                "postType", "BUSINESS_PROMOTION",
                "title", "Yeni kahve menüsü",
                "body", "Bugün yeni filtre kahveler geldi.",
                "city", "İstanbul",
                "district", "Kadıköy"
        ), token).body());

        HttpResponse<String> published = get("/api/v1/posts/published?city=İstanbul&district=Kadıköy", token);
        HttpResponse<String> socialFeed = get("/api/v1/feed/social?city=İstanbul&district=Kadıköy", token);
        HttpResponse<String> mine = get("/api/v1/me/posts", token);
        post(
                "/api/v1/posts/" + created.get("id") + "/interactions",
                Map.of(
                        "signalType", "IMPRESSION",
                        "surface", "SOCIAL_FEED",
                        "position", 0
                ),
                token
        );
        post(
                "/api/v1/posts/" + created.get("id") + "/interactions",
                Map.of(
                        "signalType", "HIDE",
                        "surface", "SOCIAL_FEED",
                        "position", 0
                ),
                token
        );
        HttpResponse<String> hiddenFeed = get("/api/v1/feed/social?city=İstanbul&district=Kadıköy", token);

        assertThat(published.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(socialFeed.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(mine.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(unwrapList(published.body())).hasSize(1);
        assertThat(unwrapList(socialFeed.body())).hasSize(1);
        assertThat(unwrapList(mine.body())).hasSize(1);
        assertThat(unwrapList(hiddenFeed.body())).isEmpty();
        assertThat(created).containsEntry("moderationStatus", "AUTO_APPROVED");
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM outbox_event", Integer.class)).isEqualTo(4);
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
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Authorization", "Bearer " + bearerToken)
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
                            ContentTestcontainers.POSTGRES.getJdbcUrl(),
                            ContentTestcontainers.POSTGRES.getUsername(),
                            ContentTestcontainers.POSTGRES.getPassword()
                    )
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        }
    }
}
