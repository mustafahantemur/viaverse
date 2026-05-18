package app.viaverse.webbff.media;

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
public class MediaProxy {
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_BODY =
            new ParameterizedTypeReference<>() {};
    private final RestClient mediaRestClient;
    private final JsonBodyParser jsonBodyParser;

    public MediaProxy(
            @Qualifier("mediaRestClient") RestClient mediaRestClient,
            JsonBodyParser jsonBodyParser
    ) {
        this.mediaRestClient = mediaRestClient;
        this.jsonBodyParser = jsonBodyParser;
    }

    public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = mediaRestClient
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
            throw new MediaProxyException(exception.getStatusCode(), jsonBodyParser.parse(exception.getResponseBodyAsString()));
        } catch (ResourceAccessException exception) {
            throw unavailable();
        }
    }

    public ProxyResponse get(String path, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = mediaRestClient
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
            throw new MediaProxyException(exception.getStatusCode(), jsonBodyParser.parse(exception.getResponseBodyAsString()));
        } catch (ResourceAccessException exception) {
            throw unavailable();
        }
    }

    private MediaProxyException unavailable() {
        return new MediaProxyException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                Map.of("code", "MEDIA_UNAVAILABLE", "message", "Media service is temporarily unavailable")
        );
    }

    public record ProxyResponse(HttpStatusCode status, Map<String, Object> body) {
    }
}
