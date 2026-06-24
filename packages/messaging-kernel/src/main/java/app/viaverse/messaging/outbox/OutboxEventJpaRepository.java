package app.viaverse.messaging.outbox;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    long countByStatus(OutboxEventStatusEnum status);

    Optional<OutboxEventJpaEntity> findFirstByStatusOrderByCreatedAtAsc(OutboxEventStatusEnum status);

    /**
     * Claim a batch of pending events that are due. Uses a pessimistic write
     * lock with {@code SKIP LOCKED} so concurrent pollers (multiple service
     * instances) do not collide on the same rows.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    List<OutboxEventJpaEntity> findByStatusAndAvailableAtLessThanEqualOrderByAvailableAtAsc(
            OutboxEventStatusEnum status,
            Instant now,
            Limit limit
    );
}
