package app.viaverse.identity.shared.messaging.outbox;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Surfaces outbox backlog state without making Kafka availability a readiness dependency.
 */
@Component("outbox")
public class OutboxHealthIndicator implements HealthIndicator {

    private final OutboxEventJpaRepository repository;
    private final Clock clock;

    public OutboxHealthIndicator(OutboxEventJpaRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public Health health() {
        long pendingCount = repository.countByStatus(OutboxEventStatusEnum.PENDING);
        long failedCount = repository.countByStatus(OutboxEventStatusEnum.FAILED);
        Instant oldestPendingCreatedAt = repository.findOldestPendingCreatedAt();

        Health.Builder builder = failedCount > 0 ? Health.down() : Health.up();
        builder.withDetail("pendingCount", pendingCount)
                .withDetail("failedCount", failedCount);
        if (oldestPendingCreatedAt != null) {
            builder.withDetail(
                    "oldestPendingAgeSeconds",
                    Duration.between(oldestPendingCreatedAt, clock.instant()).toSeconds()
            );
        }
        return builder.build();
    }
}
