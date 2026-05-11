package app.viaverse.observability.correlation;

public final class CorrelationIds {
    public static final String HEADER = "X-Correlation-Id";
    public static final String REQUEST_HEADER = "X-Request-Id";
    public static final String MDC_KEY = "correlationId";
    public static final String REQUEST_MDC_KEY = "requestId";
    public static final String PROBLEM_PROPERTY = "correlationId";
    public static final String REQUEST_PROBLEM_PROPERTY = "requestId";

    private CorrelationIds() {
    }
}
