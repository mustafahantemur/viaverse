package app.viaverse.webbff.profile;

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
public class ProfileProxy {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_BODY =
            new ParameterizedTypeReference<>() {};

    private final RestClient profileRestClient;
    private final JsonBodyParser jsonBodyParser;

    public ProfileProxy(
            @Qualifier("profileRestClient") RestClient profileRestClient,
            JsonBodyParser jsonBodyParser
    ) {
        this.profileRestClient = profileRestClient;
        this.jsonBodyParser = jsonBodyParser;
    }

    public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = profileRestClient
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
            throw new ProfileProxyException(
                    exception.getStatusCode(),
                    jsonBodyParser.parse(exception.getResponseBodyAsString())
            );
        } catch (ResourceAccessException exception) {
            throw unavailable();
        }
    }

    public ProxyResponse get(String path, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = profileRestClient
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
            throw new ProfileProxyException(
                    exception.getStatusCode(),
                    jsonBodyParser.parse(exception.getResponseBodyAsString())
            );
        } catch (ResourceAccessException exception) {
            throw unavailable();
        }
    }

    private ProfileProxyException unavailable() {
        return new ProfileProxyException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                Map.of(
                        "code", "PROFILE_UNAVAILABLE",
                        "message", "Profile service is temporarily unavailable"
                )
        );
    }

    public record ProxyResponse(HttpStatusCode status, Map<String, Object> body) {
    }
}
