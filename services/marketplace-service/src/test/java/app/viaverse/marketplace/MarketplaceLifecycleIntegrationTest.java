package app.viaverse.marketplace;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.application.port.out.ProviderEligibilityGateway;
import app.viaverse.marketplace.support.MarketplaceTestcontainers;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;
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
        classes = {
                MarketplaceServiceApplication.class,
                MarketplaceLifecycleIntegrationTest.TestEligibilityConfiguration.class
        }
)
@ActiveProfiles("test")
@ContextConfiguration(initializers = MarketplaceLifecycleIntegrationTest.FlywayInitializer.class)
class MarketplaceLifecycleIntegrationTest {

    private static final String JWT_SECRET = "test-identity-jwt-secret-change-me";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MarketplaceTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", MarketplaceTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", MarketplaceTestcontainers.POSTGRES::getPassword);
        registry.add("spring.cloud.stream.kafka.binder.brokers", MarketplaceTestcontainers.KAFKA::getBootstrapServers);
        registry.add("viaverse.marketplace.security.jwt.secret", () -> JWT_SECRET);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID requesterId;
    private UUID providerId;
    private String requesterToken;
    private String providerToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM job");
        jdbcTemplate.execute("DELETE FROM offer");
        jdbcTemplate.execute("DELETE FROM service_request_media");
        jdbcTemplate.execute("DELETE FROM service_request");
        jdbcTemplate.execute("DELETE FROM outbox_event");

        requesterId = UUID.randomUUID();
        providerId = UUID.randomUUID();
        requesterToken = accessToken(requesterId);
        providerToken = accessToken(providerId);
    }

    @Test
    void requestOfferAndJobFlowWorksEndToEnd() throws Exception {
        Map<String, Object> createdRequest = unwrap(post("/api/v1/requests", Map.of(
                "title", "Lavabo sifonu tamiri",
                "description", "Mutfak lavabosu su kaçırıyor.",
                "category", "HOME_REPAIR",
                "budgetMinAmountMinor", 50_000,
                "budgetMaxAmountMinor", 120_000,
                "currency", "TRY",
                "remoteAllowed", false,
                "district", "Kadıköy",
                "city", "İstanbul"
        ), requesterToken).body());
        String requestId = createdRequest.get("id").toString();

        post("/api/v1/requests", Map.of(
                "title", "Logo tasarımı",
                "description", "Yeni işletme logosu lazım.",
                "category", "CREATIVE_MEDIA",
                "currency", "TRY",
                "remoteAllowed", true
        ), requesterToken);

        HttpResponse<String> openRequests = get("/api/v1/requests/open", providerToken);
        assertThat(openRequests.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(unwrapList(openRequests.body())).hasSize(2);

        HttpResponse<String> workFeed = get("/api/v1/feed/work", providerToken);
        assertThat(workFeed.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(unwrapList(workFeed.body())).hasSize(1);

        Map<String, Object> submittedOffer = unwrap(post(
                "/api/v1/requests/" + requestId + "/offers",
                Map.of(
                        "amountMinor", 85_000,
                        "currency", "TRY",
                        "message", "Bugün gelebilirim."
                ),
                providerToken
        ).body());
        String offerId = submittedOffer.get("id").toString();

        HttpResponse<String> listedOffers = get("/api/v1/requests/" + requestId + "/offers", requesterToken);
        assertThat(listedOffers.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(unwrapList(listedOffers.body())).hasSize(1);

        Map<String, Object> createdJob = unwrap(post(
                "/api/v1/requests/" + requestId + "/offers/" + offerId + "/accept",
                Map.of(),
                requesterToken
        ).body());
        String jobId = createdJob.get("id").toString();
        assertThat(createdJob).containsEntry("status", "AGREED");

        Map<String, Object> startedJob = unwrap(post("/api/v1/jobs/" + jobId + "/start", Map.of(), providerToken).body());
        assertThat(startedJob).containsEntry("status", "IN_PROGRESS");

        Map<String, Object> completedJob = unwrap(post(
                "/api/v1/jobs/" + jobId + "/complete",
                Map.of(),
                requesterToken
        ).body());
        assertThat(completedJob).containsEntry("status", "COMPLETED");
        assertThat(jdbcTemplate.queryForObject("SELECT status FROM service_request WHERE id = ?::uuid", String.class, requestId))
                .isEqualTo("MATCHED");
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM outbox_event", Integer.class))
                .isEqualTo(7);
    }

    @Test
    void nonRequesterCannotReadOffers() throws Exception {
        Map<String, Object> createdRequest = unwrap(post("/api/v1/requests", Map.of(
                "title", "Küçük taşıma işi",
                "description", "İki kutu taşınacak.",
                "category", "LOCAL_HELP",
                "currency", "TRY",
                "remoteAllowed", false
        ), requesterToken).body());

        HttpResponse<String> response = get(
                "/api/v1/requests/" + createdRequest.get("id") + "/offers",
                providerToken
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void providerCanTrackAndWithdrawOwnOfferAndRequesterCanCancelOpenRequest() throws Exception {
        Map<String, Object> createdRequest = unwrap(post("/api/v1/requests", Map.of(
                "title", "Bahçe sulama",
                "description", "Haftalık sulama desteği lazım.",
                "category", "HOME_REPAIR",
                "currency", "TRY",
                "remoteAllowed", false
        ), requesterToken).body());
        String requestId = createdRequest.get("id").toString();

        Map<String, Object> submittedOffer = unwrap(post(
                "/api/v1/requests/" + requestId + "/offers",
                Map.of("amountMinor", 40_000, "currency", "TRY"),
                providerToken
        ).body());
        String offerId = submittedOffer.get("id").toString();

        assertThat(unwrapList(get("/api/v1/me/offers", providerToken).body())).hasSize(1);
        Map<String, Object> withdrawn = unwrap(post(
                "/api/v1/offers/" + offerId + "/withdraw",
                Map.of(),
                providerToken
        ).body());
        assertThat(withdrawn).containsEntry("status", "WITHDRAWN");

        Map<String, Object> cancelled = unwrap(post(
                "/api/v1/requests/" + requestId + "/cancel",
                Map.of(),
                requesterToken
        ).body());
        assertThat(cancelled).containsEntry("status", "CANCELLED");
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
                            MarketplaceTestcontainers.POSTGRES.getJdbcUrl(),
                            MarketplaceTestcontainers.POSTGRES.getUsername(),
                            MarketplaceTestcontainers.POSTGRES.getPassword()
                    )
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        }
    }

    @Configuration
    @Profile("test")
    static class TestEligibilityConfiguration {
        @Bean
        @Primary
        ProviderEligibilityGateway providerEligibilityGateway() {
            return accountId -> new ProviderEligibilityGateway.Eligibility(
                    accountId,
                    true,
                    "INDIVIDUAL_PROVIDER",
                    true,
                    false,
                    null,
                    java.util.Set.of(MarketplaceServiceCategory.HOME_REPAIR),
                    java.util.Set.of(),
                    true,
                    null,
                    null
            );
        }
    }
}
