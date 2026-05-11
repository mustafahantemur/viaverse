package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.RateLimitScope;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface AuthRateLimitBucketJpaRepository extends JpaRepository<AuthRateLimitBucketJpaEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AuthRateLimitBucketJpaEntity> findByScopeAndBucketKey(RateLimitScope scope, String bucketKey);
}
