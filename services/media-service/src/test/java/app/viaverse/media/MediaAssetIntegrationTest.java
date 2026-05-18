package app.viaverse.media;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.media.asset.application.port.out.ObjectStorageGateway;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.media.support.MediaTestcontainers;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
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

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MediaServiceApplication.class, MediaAssetIntegrationTest.TestStorageConfiguration.class}
)
@ActiveProfiles("test")
@ContextConfiguration(initializers = MediaAssetIntegrationTest.FlywayInitializer.class)
class MediaAssetIntegrationTest {
    private static final String JWT_SECRET = "test-identity-jwt-secret-change-me";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MediaTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", MediaTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", MediaTestcontainers.POSTGRES::getPassword);
        registry.add("spring.cloud.stream.kafka.binder.brokers", MediaTestcontainers.KAFKA::getBootstrapServers);
        registry.add("viaverse.media.security.jwt.secret", () -> JWT_SECRET);
    }

    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private UUID ownerId;
    private String token;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM media_upload_session");
        jdbcTemplate.execute("DELETE FROM media_asset");
        jdbcTemplate.execute("DELETE FROM outbox_event");
        ownerId = UUID.randomUUID();
        token = accessToken(ownerId);
    }

    @Test
    void uploadSessionCanBeCreatedAndCompleted() throws Exception {
        Map<String, Object> session = unwrap(post("/api/v1/assets/upload-sessions", Map.of(
                "assetKind", "IMAGE",
                "contentType", "image/jpeg",
                "originalFileName", "kahve.jpg"
        ), token).body());

        Map<String, Object> completed = unwrap(post(
                "/api/v1/assets/" + session.get("assetId") + "/complete",
                Map.of("checksumSha256", "abc123"),
                token
        ).body());

        assertThat(session.get("uploadUrl").toString()).contains("https://upload.test/");
        assertThat(completed).containsEntry("status", "READY");
        assertThat(unwrapList(get("/api/v1/me/assets", token).body())).hasSize(1);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM outbox_event", Integer.class)).isEqualTo(1);
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
                            MediaTestcontainers.POSTGRES.getJdbcUrl(),
                            MediaTestcontainers.POSTGRES.getUsername(),
                            MediaTestcontainers.POSTGRES.getPassword()
                    )
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        }
    }

    @Configuration
    @Profile("test")
    static class TestStorageConfiguration {
        @Bean
        @Primary
        ObjectStorageGateway objectStorageGateway() {
            return new ObjectStorageGateway() {
                @Override
                public UploadTarget createPresignedUpload(MediaAsset asset, Duration ttl) {
                    return new UploadTarget(
                            URI.create("https://upload.test/" + asset.getObjectKey()),
                            Map.of("Content-Type", asset.getContentType())
                    );
                }

                @Override
                public UploadedObject inspectUploadedObject(MediaAsset asset) {
                    return new UploadedObject(1024);
                }
            };
        }
    }
}
