package app.viaverse.identity.auth.infrastructure.ratelimit;

import app.viaverse.identity.auth.domain.enums.RateLimitScope;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface RateLimitBucketJpaRepository extends JpaRepository<RateLimitBucketJpaEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RateLimitBucketJpaEntity> findByScopeAndBucketKey(RateLimitScope scope, String bucketKey);
}
