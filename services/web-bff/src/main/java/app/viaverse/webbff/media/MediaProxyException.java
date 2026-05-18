package app.viaverse.webbff.media;

import java.util.Map;
import org.springframework.http.HttpStatusCode;

public class MediaProxyException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final Map<String, Object> body;

    public MediaProxyException(HttpStatusCode statusCode, Map<String, Object> body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public HttpStatusCode getStatusCode() { return statusCode; }
    public Map<String, Object> getBody() { return body; }
}
