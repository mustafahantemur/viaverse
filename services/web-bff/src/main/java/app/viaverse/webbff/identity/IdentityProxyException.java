package app.viaverse.webbff.identity;

import java.util.Map;
import org.springframework.http.HttpStatusCode;

/**
 * Thrown when identity-service returns a non-2xx. Carries the upstream
 * status + parsed JSON body so the controller can re-emit them unchanged.
 */
public class IdentityProxyException extends RuntimeException {
    private final HttpStatusCode status;
    private final transient Map<String, Object> body;

    public IdentityProxyException(HttpStatusCode status, Map<String, Object> body) {
        super("identity-service returned " + status);
        this.status = status;
        this.body = body;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public Map<String, Object> getBody() {
        return body;
    }
}
