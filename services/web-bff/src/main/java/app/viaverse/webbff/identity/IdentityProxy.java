package app.viaverse.webbff.identity;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

/**
 * Thin HTTP forwarder to identity-service. Carries the caller's bearer
 * token through (if any), preserves the wire shape so error responses
 * surface to the client exactly as identity produced them, and bubbles
 * non-2xx HTTP status up via {@link IdentityProxyException} so the
 * controller layer can decide whether to translate them.
 *
 * <p>The proxy intentionally doesn't validate JWTs — identity-service is
 * the source of truth for authn/authz; double-validation would just
 * couple the BFF to identity's signing keys.
 */
@Component
public class IdentityProxy {

    private final RestClient identityRestClient;

    public IdentityProxy(RestClient identityRestClient) {
        this.identityRestClient = identityRestClient;
    }

    public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = identityRestClient
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
                    .toEntity(new org.springframework.core.ParameterizedTypeReference<>() {});
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            Map<String, Object> body0 = parseErrorBody(exception.getResponseBodyAsString());
            throw new IdentityProxyException(exception.getStatusCode(), body0);
        }
    }

    public ProxyResponse get(String path, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = identityRestClient
                    .get()
                    .uri(path)
                    .headers(headers -> {
                        if (authorization != null && !authorization.isBlank()) {
                            headers.add("Authorization", authorization);
                        }
                    })
                    .retrieve()
                    .toEntity(new org.springframework.core.ParameterizedTypeReference<>() {});
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            Map<String, Object> body0 = parseErrorBody(exception.getResponseBodyAsString());
            throw new IdentityProxyException(exception.getStatusCode(), body0);
        }
    }

    private Map<String, Object> parseErrorBody(String raw) {
        if (raw == null || raw.isBlank()) {
            return Map.of();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(raw, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        } catch (Exception exception) {
            return Map.of("raw", raw);
        }
    }

    public record ProxyResponse(HttpStatusCode status, Map<String, Object> body) {}
}
