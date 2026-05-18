package app.viaverse.webbff.marketplace;

import java.util.Map;
import org.springframework.http.HttpStatusCode;

public class MarketplaceProxyException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final Map<String, Object> body;

    public MarketplaceProxyException(HttpStatusCode statusCode, Map<String, Object> body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getBody() {
        return body;
    }
}
