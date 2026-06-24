package app.viaverse.trustgamification.support;

import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class TrustGamificationTestcontainers {

    public static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(DockerImageName.parse("postgres:17.5"))
                    .withDatabaseName("viaverse_trust_gamification_test")
                    .withUsername("viaverse")
                    .withPassword("viaverse");

    public static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("apache/kafka:4.0.0"));

    static {
        POSTGRES.start();
        KAFKA.start();
    }

    private TrustGamificationTestcontainers() {
    }
}
