package app.viaverse.shared.kernel.error;

/**
 * Marker for failures that can tell callers when a retry becomes meaningful again.
 */
public interface RetryAfterAware {
    long retryAfterSeconds();
}
