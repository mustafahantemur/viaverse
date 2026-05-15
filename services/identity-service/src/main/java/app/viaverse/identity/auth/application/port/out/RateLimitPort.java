package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.RateLimitScope;
import java.time.Duration;

public interface RateLimitPort {

    Result incrementAndCheck(RateLimitScope scope, String key, int limit, Duration window);

    record Result(boolean allowed, long currentCount, long ttlSeconds) {}
}
