package app.viaverse.identity.shared.error;

import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.RetryAfterAware;
import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends IdentityException implements RetryAfterAware {
    private final long retryAfterSeconds;

    public RateLimitExceededException(long retryAfterSeconds) {
        super(AppErrorCode.AUTH_RATE_LIMITED, "Too many authentication attempts", HttpStatus.TOO_MANY_REQUESTS);
        this.retryAfterSeconds = Math.max(1, retryAfterSeconds);
    }

    public long retryAfterSeconds() {
        return retryAfterSeconds;
    }
}
