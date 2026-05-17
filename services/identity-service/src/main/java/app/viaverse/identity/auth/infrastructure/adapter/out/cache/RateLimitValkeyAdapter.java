package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.application.port.out.RateLimitPort;
import app.viaverse.identity.auth.domain.enums.RateLimitScopeEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Duration;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class RateLimitValkeyAdapter implements RateLimitPort {

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> rateLimitScript;

    public RateLimitValkeyAdapter(StringRedisTemplate redis, DefaultRedisScript<Long> rateLimitScript) {
        this.redis = redis;
        this.rateLimitScript = rateLimitScript;
    }

    @Override
    public Result incrementAndCheck(RateLimitScopeEnum scope, String key, int limit, Duration window) {
        return incrementInternal(scope, key, limit, window);
    }

    @Override
    public Result peek(RateLimitScopeEnum scope, String key, int limit, Duration window) {
        String bucketKey = ValkeyKeyScheme.rateLimit(scope, key);
        try {
            String raw = redis.opsForValue().get(bucketKey);
            long count = raw == null ? 0L : parseCount(raw);
            Long ttl = redis.getExpire(bucketKey);
            // peek does not create / extend the bucket. {@code allowed} compares
            // against {@code limit} strictly so a bucket that is *at* the limit
            // still blocks further calls. {@code window} kept in the signature
            // for symmetry with the recording calls; ignored on a pure read.
            assert window != null;
            return new Result(count < limit, count, ttl == null ? 0L : ttl);
        } catch (DataAccessException exception) {
            throw IdentityErrors.rateLimitBackendUnavailable(exception);
        }
    }

    @Override
    public Result recordFailure(RateLimitScopeEnum scope, String key, int limit, Duration window) {
        // Semantically identical to incrementAndCheck (atomic INCR + TTL set on
        // first write), but kept as a distinct method so call sites document
        // intent: "bump this bucket because the operation failed".
        return incrementInternal(scope, key, limit, window);
    }

    private Result incrementInternal(RateLimitScopeEnum scope, String key, int limit, Duration window) {
        String bucketKey = ValkeyKeyScheme.rateLimit(scope, key);
        Long count;
        Long ttl;
        try {
            count = redis.execute(
                    rateLimitScript,
                    List.of(bucketKey),
                    String.valueOf(window.toSeconds())
            );
            ttl = redis.getExpire(bucketKey);
        } catch (DataAccessException exception) {
            throw IdentityErrors.rateLimitBackendUnavailable(exception);
        }
        // Fail closed: a null script result means the script did not execute as
        // expected (e.g. transient backend issue). Treating it as 0 would let
        // every request through unthrottled.
        if (count == null) {
            throw IdentityErrors.rateLimitBackendUnavailable(null);
        }
        return new Result(count <= limit, count, ttl == null ? 0L : ttl);
    }

    private static long parseCount(String raw) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }
}
