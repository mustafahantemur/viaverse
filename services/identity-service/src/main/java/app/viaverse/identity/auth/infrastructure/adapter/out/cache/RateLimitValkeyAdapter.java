package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.application.port.out.RateLimitPort;
import app.viaverse.identity.auth.domain.enums.RateLimitScopeEnum;
import app.viaverse.identity.auth.infrastructure.adapter.out.cache.ValkeyKeyScheme;
import java.time.Duration;
import java.util.List;
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
        String bucketKey = ValkeyKeyScheme.rateLimit(scope, key);
        Long count = redis.execute(
                rateLimitScript,
                List.of(bucketKey),
                String.valueOf(window.toSeconds())
        );
        long current = count == null ? 0L : count;
        Long ttl = redis.getExpire(bucketKey);
        return new Result(current <= limit, current, ttl == null ? 0L : ttl);
    }
}
