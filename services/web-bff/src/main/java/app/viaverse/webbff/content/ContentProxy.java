package app.viaverse.webbff.content;

import app.viaverse.webbff.identity.JsonBodyParser;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class ContentProxy {
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_BODY =
            new ParameterizedTypeReference<>() {};
    private final RestClient contentRestClient;
    private final JsonBodyParser jsonBodyParser;

    public ContentProxy(
            @Qualifier("contentRestClient") RestClient contentRestClient,
            JsonBodyParser jsonBodyParser
    ) {
        this.contentRestClient = contentRestClient;
        this.jsonBodyParser = jsonBodyParser;
    }

    public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = contentRestClient
                    .method(method)
                    .uri(path)
                    .headers(headers -> {
                        headers.add("Content-Type", "application/json");
                        if (authorization != null && !authorization.isBlank()) {
                            headers.add("Authorization", authorization);
                        }
                    })
                    .body(body == null ? new HashMap<>() : body)
                    .retrieve()
                    .toEntity(MAP_BODY);
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            throw new ContentProxyException(
                    exception.getStatusCode(),
                    jsonBodyParser.parse(exception.getResponseBodyAsString())
            );
        } catch (ResourceAccessException exception) {
            throw unavailable();
        }
    }

    public ProxyResponse get(String path, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = contentRestClient
                    .get()
                    .uri(path)
                    .headers(headers -> {
                        if (authorization != null && !authorization.isBlank()) {
                            headers.add("Authorization", authorization);
                        }
                    })
                    .retrieve()
                    .toEntity(MAP_BODY);
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            throw new ContentProxyException(
                    exception.getStatusCode(),
                    jsonBodyParser.parse(exception.getResponseBodyAsString())
            );
        } catch (ResourceAccessException exception) {
            throw unavailable();
        }
    }

    private ContentProxyException unavailable() {
        return new ContentProxyException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                Map.of("code", "CONTENT_UNAVAILABLE", "message", "Content service is temporarily unavailable")
        );
    }

    public record ProxyResponse(HttpStatusCode status, Map<String, Object> body) {
    }
}
