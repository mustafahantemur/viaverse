package app.viaverse.adminbff.application;

import app.viaverse.adminbff.config.AdminBffProperties;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class AdminBusinessProfileProxy {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_BODY =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final AdminBffProperties properties;

    public AdminBusinessProfileProxy(
            @Qualifier("adminProfileRestClient") RestClient restClient,
            AdminBffProperties properties
    ) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public ProxyResponse get(String path) {
        try {
            ResponseEntity<Map<String, Object>> response = restClient.get()
                    .uri(path)
                    .header(INTERNAL_TOKEN_HEADER, properties.getInternalApiToken())
                    .retrieve()
                    .toEntity(MAP_BODY);
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            return new ProxyResponse(exception.getStatusCode(), Map.of());
        } catch (ResourceAccessException exception) {
            return unavailable();
        }
    }

    public ProxyResponse post(String path, Object body) {
        try {
            ResponseEntity<Map<String, Object>> response = restClient.post()
                    .uri(path)
                    .header(INTERNAL_TOKEN_HEADER, properties.getInternalApiToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(MAP_BODY);
            return new ProxyResponse(response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException exception) {
            return new ProxyResponse(exception.getStatusCode(), Map.of());
        } catch (ResourceAccessException exception) {
            return unavailable();
        }
    }

    private ProxyResponse unavailable() {
        return new ProxyResponse(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                Map.of("code", "PROFILE_UNAVAILABLE", "message", "Profile service is unavailable")
        );
    }

    public record ProxyResponse(HttpStatusCode status, Map<String, Object> body) {
    }
}
