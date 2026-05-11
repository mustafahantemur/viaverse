package app.viaverse.observability.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public final class CorrelationIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = request.getHeader(CorrelationIds.HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        String requestId = request.getHeader(CorrelationIds.REQUEST_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(CorrelationIds.MDC_KEY, correlationId);
        MDC.put(CorrelationIds.REQUEST_MDC_KEY, requestId);
        response.setHeader(CorrelationIds.HEADER, correlationId);
        response.setHeader(CorrelationIds.REQUEST_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CorrelationIds.REQUEST_MDC_KEY);
            MDC.remove(CorrelationIds.MDC_KEY);
        }
    }
}
