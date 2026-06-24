package app.viaverse.identity.support;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Singleton-style Testcontainers holder for identity-service integration tests.
 *
 * <p>Starts a Postgres, Valkey (Redis-compatible), and Kafka container once per JVM
 * and exposes them as static fields for use in {@code @DynamicPropertySource} bridges
 * and {@code ApplicationContextInitializer}s.
 *
 * <p>Reuse is enabled on each container. To benefit from cross-run reuse, developers
 * must opt-in by enabling Testcontainers reuse on their machine, e.g. by adding
 * {@code testcontainers.reuse.enable=true} to {@code ~/.testcontainers.properties}.
 */
public final class IdentityTestcontainers {

    public static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:18-alpine"))
                    .withDatabaseName("viaverse_identity_test")
                    .withUsername("viaverse")
                    .withPassword("viaverse")
                    .withReuse(true);

    public static final GenericContainer<?> VALKEY =
            new GenericContainer<>(DockerImageName.parse("valkey/valkey:9-alpine"))
                    .withExposedPorts(6379)
                    .withReuse(true);

    public static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"))
                    .withReuse(true);

    static {
        POSTGRES.start();
        VALKEY.start();
        KAFKA.start();
    }

    private IdentityTestcontainers() {}
}
