package app.viaverse.identity.shared.messaging.outbox;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    /**
     * Claim a batch of pending events that are due. Uses a pessimistic write
     * lock with {@code SKIP LOCKED} so concurrent pollers (multiple service
     * instances) do not collide on the same rows.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    @Query("""
            SELECT e FROM OutboxEventJpaEntity e
            WHERE e.status = app.viaverse.identity.shared.messaging.outbox.OutboxEventStatusEnum.PENDING
              AND e.availableAt <= :now
            ORDER BY e.availableAt ASC
            """)
    List<OutboxEventJpaEntity> claimPendingBatch(@Param("now") Instant now, Limit limit);
}
