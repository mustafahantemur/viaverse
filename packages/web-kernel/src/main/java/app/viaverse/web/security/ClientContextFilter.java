package app.viaverse.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Populates MDC with resolved client metadata for the lifetime of each HTTP request.
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
                MDC.put(ClientContextMdc.CLIENT_IP, clientIp);
            }
            if (userAgent != null && !userAgent.isBlank()) {
                MDC.put(ClientContextMdc.CLIENT_USER_AGENT, userAgent);
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(ClientContextMdc.CLIENT_IP);
            MDC.remove(ClientContextMdc.CLIENT_USER_AGENT);
        }
    }
}
