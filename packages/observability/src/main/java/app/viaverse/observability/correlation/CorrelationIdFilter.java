package app.viaverse.observability.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Establishes stable request and correlation identifiers for every HTTP request.
 */
public final class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_HEADER = "X-Correlation-Id";
    private static final String REQUEST_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = headerOrGenerated(request, CORRELATION_HEADER);
        String requestId = headerOrGenerated(request, REQUEST_HEADER);
        try {
            MDC.put(CorrelationIds.MDC_KEY, correlationId);
            MDC.put(CorrelationIds.REQUEST_MDC_KEY, requestId);
            response.setHeader(CORRELATION_HEADER, correlationId);
            response.setHeader(REQUEST_HEADER, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CorrelationIds.MDC_KEY);
            MDC.remove(CorrelationIds.REQUEST_MDC_KEY);
        }
    }

    private String headerOrGenerated(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        return value == null || value.isBlank() ? UUID.randomUUID().toString() : value.trim();
    }
}
