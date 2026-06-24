package app.viaverse.observability.audit;

/**
 * Cross-cutting context attached to an {@link AuditEvent}. All fields are
 * optional — a service that does not yet populate them simply records null.
 *
 * @param correlationId the cross-service trace correlation id
 * @param requestId     the per-request id
 * @param source        the logical service / subsystem emitting the event
 * @param sourceIp      the resolved client IP for the originating request
 * @param userAgent     the client User-Agent header for the originating request
 */
public record AuditContext(
        String correlationId,
        String requestId,
        String source,
        String sourceIp,
        String userAgent
) {
    public AuditContext(String correlationId, String requestId, String source) {
        this(correlationId, requestId, source, null, null);
    }
}
