package app.viaverse.identity.auth.infrastructure.ratelimit;

import app.viaverse.identity.auth.domain.enums.RateLimitScope;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AuthRateLimiter {
    private final AuthProperties properties;
    private final RateLimitBucketJpaRepository bucketRepository;
    private final TokenHasher tokenHasher;

    public AuthRateLimiter(
            AuthProperties properties,
            RateLimitBucketJpaRepository bucketRepository,
            TokenHasher tokenHasher
    ) {
        this.properties = properties;
        this.bucketRepository = bucketRepository;
        this.tokenHasher = tokenHasher;
    }

    public void checkAndIncrement(RateLimitScope scope, String rawKey, long windowSeconds, int maxAttempts) {
        if (!properties.getRateLimit().isEnabled() || rawKey == null || rawKey.isBlank()) {
            return;
        }

        Instant now = Instant.now();
        String bucketKey = bucketKey(scope, rawKey);
        RateLimitBucketJpaEntity bucket = bucketRepository.findByScopeAndBucketKey(scope, bucketKey)
                .orElse(null);
        if (bucket == null) {
            bucketRepository.save(new RateLimitBucketJpaEntity(
                    UUID.randomUUID(),
                    scope,
                    bucketKey,
                    now,
                    now
            ));
            return;
        }

        if (bucket.getLockedUntil() != null && bucket.getLockedUntil().isAfter(now)) {
            throw new RateLimitExceededException(secondsUntil(now, bucket.getLockedUntil()));
        }

        Instant windowEnd = bucket.getWindowStart().plusSeconds(windowSeconds);
        if (!windowEnd.isAfter(now)) {
            bucket.resetWindow(now);
            return;
        }

        if (bucket.getAttemptCount() >= maxAttempts) {
            bucket.lockUntil(windowEnd, now);
            throw new RateLimitExceededException(secondsUntil(now, windowEnd));
        }

        bucket.increment(now);
    }

    public void lock(RateLimitScope scope, String rawKey, long durationSeconds) {
        if (!properties.getRateLimit().isEnabled() || rawKey == null || rawKey.isBlank()) {
            return;
        }

        Instant now = Instant.now();
        Instant lockedUntil = now.plusSeconds(durationSeconds);
        String bucketKey = bucketKey(scope, rawKey);
        RateLimitBucketJpaEntity bucket = bucketRepository.findByScopeAndBucketKey(scope, bucketKey)
                .orElseGet(() -> bucketRepository.save(new RateLimitBucketJpaEntity(
                        UUID.randomUUID(),
                        scope,
                        bucketKey,
                        now,
                        now
                )));
        bucket.lockUntil(lockedUntil, now);
    }

    public void ensureNotLocked(RateLimitScope scope, String rawKey) {
        if (!properties.getRateLimit().isEnabled() || rawKey == null || rawKey.isBlank()) {
            return;
        }

        Instant now = Instant.now();
        bucketRepository.findByScopeAndBucketKey(scope, bucketKey(scope, rawKey))
                .filter(bucket -> bucket.getLockedUntil() != null && bucket.getLockedUntil().isAfter(now))
                .ifPresent(bucket -> {
                    throw new RateLimitExceededException(secondsUntil(now, bucket.getLockedUntil()));
                });
    }

    private String bucketKey(RateLimitScope scope, String rawKey) {
        return tokenHasher.hash(scope.name() + ":" + rawKey.trim().toLowerCase());
    }

    private long secondsUntil(Instant now, Instant target) {
        return Math.max(1, target.getEpochSecond() - now.getEpochSecond());
    }
}
