package app.viaverse.profile;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.contracts.identity.account.IdentityAccountEventTypes;
import app.viaverse.profile.support.ProfileTestcontainers;
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
@ContextConfiguration(initializers = ProfileProvisioningIntegrationTest.FlywayInitializer.class)
class ProfileProvisioningIntegrationTest {

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", ProfileTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", ProfileTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", ProfileTestcontainers.POSTGRES::getPassword);
        registry.add("spring.cloud.stream.kafka.binder.brokers", ProfileTestcontainers.KAFKA::getBootstrapServers);
    }

    @Autowired
    @Qualifier("identityAccountEventsConsumer")
    private Consumer<Message<Map<String, Object>>> consumer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanState() {
        jdbcTemplate.execute("DELETE FROM consumed_event");
        jdbcTemplate.execute("DELETE FROM profile_block");
        jdbcTemplate.execute("DELETE FROM profile_preference");
        jdbcTemplate.execute("DELETE FROM business_profile");
        jdbcTemplate.execute("DELETE FROM individual_provider_profile");
        jdbcTemplate.execute("DELETE FROM profile_capability");
        jdbcTemplate.execute("DELETE FROM profile_trust_snapshot");
        jdbcTemplate.execute("DELETE FROM profile");
        jdbcTemplate.execute("DELETE FROM outbox_event");
    }

    @Test
    void accountCreatedEventProvisionsProfileAndQueuesProfileCreatedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        consumer.accept(accountCreatedMessage(eventId, accountId));

        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM profile WHERE account_id = ?",
                Integer.class,
                accountId
        )).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT completeness_score FROM profile WHERE account_id = ?",
                Integer.class,
                accountId
        )).isEqualTo(50);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM consumed_event WHERE event_id = ?",
                Integer.class,
                eventId
        )).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM profile_capability WHERE account_id = ? AND capability = 'CUSTOMER'",
                Integer.class,
                accountId
        )).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM outbox_event WHERE event_type = ?",
                Integer.class,
                "profile.ProfileCreated.v1"
        )).isEqualTo(1);
    }

    @Test
    void replayedAccountCreatedEventIsIdempotent() {
        UUID eventId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Message<Map<String, Object>> message = accountCreatedMessage(eventId, accountId);

        consumer.accept(message);
        consumer.accept(message);

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM profile", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM consumed_event", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM outbox_event", Integer.class)).isEqualTo(1);
    }

    private Message<Map<String, Object>> accountCreatedMessage(UUID eventId, UUID accountId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", eventId.toString());
        payload.put("occurredAt", Instant.parse("2026-05-18T08:00:00Z").toString());
        payload.put("version", "v1");
        payload.put("accountId", accountId.toString());
        payload.put("displayName", "Ada Lovelace");
        payload.put("firstName", "Ada");
        payload.put("lastName", "Lovelace");
        return MessageBuilder.withPayload(payload)
                .setHeader("eventType", IdentityAccountEventTypes.ACCOUNT_CREATED_V1)
                .build();
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
