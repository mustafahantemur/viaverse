package app.viaverse.identity.shared.security;

import app.viaverse.identity.shared.aspect.AuditEventAspect;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Populates MDC with the resolved client IP and User-Agent for the lifetime
 * of each HTTP request. {@code AuditEventAspect} reads these values to record
 * forensic context on every audit log entry, and {@code ObservedActionAspect}
 * picks them up as structured log fields.
 *
 * <p>Always clears MDC in a {@code finally} block so background threads or
 * reused request threads cannot leak context across requests.
 */
@Component
public final class ClientContextFilter extends OncePerRequestFilter {

    private final ClientIpResolver clientIpResolver;

    public ClientContextFilter(ClientIpResolver clientIpResolver) {
        this.clientIpResolver = clientIpResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String clientIp = clientIpResolver.resolve(request);
        String userAgent = request.getHeader("User-Agent");
        try {
            if (clientIp != null && !clientIp.isBlank()) {
                MDC.put(AuditEventAspect.MDC_CLIENT_IP, clientIp);
            }
            if (userAgent != null && !userAgent.isBlank()) {
                MDC.put(AuditEventAspect.MDC_CLIENT_USER_AGENT, userAgent);
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(AuditEventAspect.MDC_CLIENT_IP);
            MDC.remove(AuditEventAspect.MDC_CLIENT_USER_AGENT);
        }
    }
}
