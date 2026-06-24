package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.observability.correlation.CorrelationIdProvider;
import app.viaverse.observability.correlation.CorrelationIds;
import app.viaverse.shared.kernel.error.AppErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public final class IdentityAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CorrelationIdProvider correlationIdProvider = new CorrelationIdProvider();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        AppErrorCode identityCode = identityCode(request, authException);
        LOGGER.atWarn()
                .addKeyValue("event.action", "auth.bearer")
                .addKeyValue("event.outcome", "failure")
                .addKeyValue("error.code", identityCode)
                .log("bearer authentication failed");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem(identityCode));
    }

    private AppErrorCode identityCode(HttpServletRequest request, AuthenticationException exception) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isBlank()) {
            return AppErrorCode.AUTH_BEARER_TOKEN_REQUIRED;
        }
        String message = exception.getMessage();
        if (message != null && message.toLowerCase().contains("expired")) {
            return AppErrorCode.AUTH_ACCESS_TOKEN_EXPIRED;
        }
        return AppErrorCode.AUTH_INVALID_ACCESS_TOKEN;
    }

    private Map<String, Object> problem(AppErrorCode identityCode) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("title", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("detail", detail(identityCode));
        body.put("code", AppErrorCode.UNAUTHORIZED.name());
        body.put("identityCode", identityCode.name());
        String correlationId = correlationIdProvider.currentCorrelationId();
        if (correlationId != null && !correlationId.isBlank()) {
            body.put(CorrelationIds.PROBLEM_PROPERTY, correlationId);
        }
        String requestId = correlationIdProvider.currentRequestId();
        if (requestId != null && !requestId.isBlank()) {
            body.put(CorrelationIds.REQUEST_PROBLEM_PROPERTY, requestId);
        }
        return body;
    }

    private String detail(AppErrorCode identityCode) {
        if (identityCode == AppErrorCode.AUTH_BEARER_TOKEN_REQUIRED) {
            return "Bearer token is required";
        }
        if (identityCode == AppErrorCode.AUTH_ACCESS_TOKEN_EXPIRED) {
            return "Access token expired";
        }
        return "Invalid access token";
    }
}
