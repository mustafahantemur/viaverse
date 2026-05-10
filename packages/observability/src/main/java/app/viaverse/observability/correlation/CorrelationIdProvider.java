package app.viaverse.observability.correlation;

import org.slf4j.MDC;

public final class CorrelationIdProvider {
    public String currentCorrelationId() {
        return MDC.get(CorrelationIds.MDC_KEY);
    }
}

