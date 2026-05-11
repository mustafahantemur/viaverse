package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.RateLimitScope;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_rate_limit_bucket")
public class AuthRateLimitBucketJpaEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 64)
    private RateLimitScope scope;

    @Column(name = "bucket_key", nullable = false, length = 128)
    private String bucketKey;

    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthRateLimitBucketJpaEntity() {
    }

    public AuthRateLimitBucketJpaEntity(
            UUID id,
            RateLimitScope scope,
            String bucketKey,
            Instant windowStart,
            Instant now
    ) {
        this.id = id;
        this.scope = scope;
        this.bucketKey = bucketKey;
        this.windowStart = windowStart;
        this.attemptCount = 1;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Instant getWindowStart() {
        return windowStart;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void resetWindow(Instant now) {
        this.windowStart = now;
        this.attemptCount = 1;
        this.lockedUntil = null;
        this.updatedAt = now;
    }

    public void increment(Instant now) {
        this.attemptCount++;
        this.updatedAt = now;
    }

    public void lockUntil(Instant lockedUntil, Instant now) {
        this.lockedUntil = lockedUntil;
        this.updatedAt = now;
    }
}
