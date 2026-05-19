package app.viaverse.webbff.identity;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

/**
 * HTTP forwarder to identity-service. Carries the caller's bearer token
 * through (if any), preserves identity's wire shape so error responses
 * surface to the client unchanged, and turns non-2xx into
 * {@link IdentityProxyException} so controllers don't have to mix happy-path
 * and error logic. {@link IdentityProxyExceptionHandler} maps that exception
 * back into the original status + body for the client.
 *
 * <p>The proxy intentionally doesn't validate JWTs — identity-service is
 * the source of truth for authn/authz; double-validation would just couple
 * the BFF to identity's signing keys.
 */
@Component
public class IdentityProxy {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_BODY =
            new ParameterizedTypeReference<>() {};

    private final RestClient identityRestClient;
    private final JsonBodyParser jsonBodyParser;

    public IdentityProxy(
            @Qualifier("identityRestClient") RestClient identityRestClient,
            JsonBodyParser jsonBodyParser
    ) {
        this.identityRestClient = identityRestClient;
        this.jsonBodyParser = jsonBodyParser;
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
                    .toEntity(MAP_BODY);
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            throw new IdentityProxyException(
                    exception.getStatusCode(),
                    jsonBodyParser.parse(exception.getResponseBodyAsString())
            );
        } catch (ResourceAccessException exception) {
            throw unavailable();
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
                    .toEntity(MAP_BODY);
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            throw new IdentityProxyException(
                    exception.getStatusCode(),
                    jsonBodyParser.parse(exception.getResponseBodyAsString())
            );
        } catch (ResourceAccessException exception) {
            throw unavailable();
        }
    }

    private IdentityProxyException unavailable() {
        return new IdentityProxyException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                Map.of(
                        "code", "IDENTITY_UNAVAILABLE",
                        "message", "Identity service is temporarily unavailable"
                )
        );
    }

    public record ProxyResponse(HttpStatusCode status, Map<String, Object> body) {}
}
