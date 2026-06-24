package app.viaverse.identity;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.contracts.profile.profile.ProfileEventTypes;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.support.IdentityTestcontainers;
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
@ContextConfiguration(initializers = IdentityProfileMirrorIntegrationTest.FlywayInitializer.class)
class IdentityProfileMirrorIntegrationTest {

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", IdentityTestcontainers.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", IdentityTestcontainers.POSTGRES::getUsername);
        registry.add("spring.datasource.password", IdentityTestcontainers.POSTGRES::getPassword);
        registry.add("spring.data.redis.host", IdentityTestcontainers.VALKEY::getHost);
        registry.add("spring.data.redis.port", () -> IdentityTestcontainers.VALKEY.getMappedPort(6379));
        registry.add("spring.cloud.stream.kafka.binder.brokers", IdentityTestcontainers.KAFKA::getBootstrapServers);
    }

    @Autowired
    @Qualifier("profileEventsConsumer")
    private Consumer<Message<Map<String, Object>>> consumer;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID accountId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM consumed_event");

        accountId = UUID.randomUUID();
        accountRepository.save(Account.register(
                accountId,
                "Ada Lovelace",
                Instant.parse("2026-05-18T08:00:00Z")
        ));
    }

    @Test
    void profileUpdatedEventMirrorsDisplayFieldsIntoIdentityReadModel() {
        UUID eventId = UUID.randomUUID();

        consumer.accept(profileUpdatedMessage(eventId, accountId));

        assertThat(jdbcTemplate.queryForObject(
                "SELECT display_name FROM identity_account WHERE id = ?",
                String.class,
                accountId
        )).isEqualTo("Ada Byron");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT first_name FROM identity_account WHERE id = ?",
                String.class,
                accountId
        )).isEqualTo("Ada");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT last_name FROM identity_account WHERE id = ?",
                String.class,
                accountId
        )).isEqualTo("Byron");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM consumed_event WHERE event_id = ?",
                Integer.class,
                eventId
        )).isEqualTo(1);
    }

    @Test
    void replayedProfileUpdatedEventIsIdempotent() {
        UUID eventId = UUID.randomUUID();
        Message<Map<String, Object>> message = profileUpdatedMessage(eventId, accountId);

        consumer.accept(message);
        consumer.accept(message);

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM consumed_event", Integer.class)).isEqualTo(1);
    }

    private Message<Map<String, Object>> profileUpdatedMessage(UUID eventId, UUID currentAccountId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", eventId.toString());
        payload.put("occurredAt", Instant.parse("2026-05-18T09:00:00Z").toString());
        payload.put("version", "v1");
        payload.put("accountId", currentAccountId.toString());
        payload.put("displayName", "Ada Byron");
        payload.put("firstName", "Ada");
        payload.put("lastName", "Byron");
        payload.put("avatarMediaId", null);
        payload.put("headline", "Mathematician");
        payload.put("publicVisibility", "PUBLIC");
        return MessageBuilder.withPayload(payload)
                .setHeader("eventType", ProfileEventTypes.PROFILE_UPDATED_V1)
                .build();
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
