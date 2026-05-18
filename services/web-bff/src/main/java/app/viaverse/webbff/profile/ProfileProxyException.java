package app.viaverse.webbff.profile;

import java.util.Map;
import org.springframework.http.HttpStatusCode;

public class ProfileProxyException extends RuntimeException {

    private final HttpStatusCode status;
    private final transient Map<String, Object> body;

    public ProfileProxyException(HttpStatusCode status, Map<String, Object> body) {
        super("profile-service returned " + status);
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
