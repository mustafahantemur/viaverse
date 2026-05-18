package app.viaverse.trustgamification;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.contracts.profile.profile.ProfileEventTypes;
import app.viaverse.trustgamification.support.TrustGamificationTestcontainers;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TrustProvisioningIntegrationTest.FlywayInitializer.class)
class TrustProvisioningIntegrationTest {

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TrustGamificationTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", TrustGamificationTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", TrustGamificationTestcontainers.POSTGRES::getPassword);
        registry.add("spring.cloud.stream.kafka.binder.brokers", TrustGamificationTestcontainers.KAFKA::getBootstrapServers);
    }

    @Autowired
    @Qualifier("profileEventsConsumer")
    private Consumer<Message<Map<String, Object>>> consumer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanState() {
        jdbcTemplate.execute("DELETE FROM consumed_event");
        jdbcTemplate.execute("DELETE FROM trust_state");
        jdbcTemplate.execute("DELETE FROM outbox_event");
    }

    @Test
    void profileCreatedEventBootstrapsBaselineTrustAndQueuesScoreEvent() {
        UUID eventId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        consumer.accept(profileCreatedMessage(eventId, accountId));

        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM trust_state WHERE account_id = ?",
                Integer.class,
                accountId
        )).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT score FROM trust_state WHERE account_id = ?",
                Integer.class,
                accountId
        )).isEqualTo(100);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT trust_level FROM trust_state WHERE account_id = ?",
                String.class,
                accountId
        )).isEqualTo("BASIC");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM outbox_event WHERE event_type = ?",
                Integer.class,
                "trust.TrustScoreUpdated.v1"
        )).isEqualTo(1);
    }

    @Test
    void replayedProfileCreatedEventIsIdempotent() {
        UUID eventId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Message<Map<String, Object>> message = profileCreatedMessage(eventId, accountId);

        consumer.accept(message);
        consumer.accept(message);

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM trust_state", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM consumed_event", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM outbox_event", Integer.class)).isEqualTo(1);
    }

    private Message<Map<String, Object>> profileCreatedMessage(UUID eventId, UUID accountId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", eventId.toString());
        payload.put("occurredAt", Instant.parse("2026-05-18T08:00:00Z").toString());
        payload.put("version", "v1");
        payload.put("accountId", accountId.toString());
        payload.put("displayName", "Ada Lovelace");
        payload.put("publicVisibility", "LIMITED");
        return MessageBuilder.withPayload(payload)
                .setHeader("eventType", ProfileEventTypes.PROFILE_CREATED_V1)
                .build();
    }

    static class FlywayInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Flyway.configure()
                    .dataSource(
                            TrustGamificationTestcontainers.POSTGRES.getJdbcUrl(),
                            TrustGamificationTestcontainers.POSTGRES.getUsername(),
                            TrustGamificationTestcontainers.POSTGRES.getPassword()
                    )
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        }
    }
}
