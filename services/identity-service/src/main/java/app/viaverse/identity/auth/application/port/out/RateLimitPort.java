package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.RateLimitScopeEnum;
import java.time.Duration;

/**
 * Outbound port for the rate-limit store.
 *
 * <p>The legacy {@link #incrementAndCheck} primitive is suitable for
 * <em>throttling-by-attempt</em> buckets (every call, success or fail,
 * counts toward the limit — e.g. /start, /forgot-password/start). It is
 * <em>not</em> appropriate for password-login style protection where a
 * successful authentication should not burn the user's failure budget.
 * For that case the new {@link #peek} + {@link #recordFailure} pair lets
 * callers gate first and only bump the bucket on a real failure.
 */
public interface RateLimitPort {

    /**
     * Atomically increment the bucket's counter and report whether the
     * caller is still within {@code limit}. Use for endpoints where every
     * call costs one slot regardless of outcome.
     */
    Result incrementAndCheck(RateLimitScopeEnum scope, String key, int limit, Duration window);

    /**
     * Read the current bucket count without mutating it. Use this to
     * fail-fast before performing the operation when a successful outcome
     * should NOT count against the limit (e.g. password-login).
     */
    Result peek(RateLimitScopeEnum scope, String key, int limit, Duration window);

    /**
     * Bump the bucket counter by one (e.g. on a confirmed failure). Returns
     * the post-increment {@link Result} so the caller can choose to lock
     * the account / IP on overflow.
     */
    Result recordFailure(RateLimitScopeEnum scope, String key, int limit, Duration window);

    record Result(boolean allowed, long currentCount, long ttlSeconds) {}
}
