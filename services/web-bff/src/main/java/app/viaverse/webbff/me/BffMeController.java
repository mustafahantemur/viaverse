package app.viaverse.webbff.me;

import app.viaverse.webbff.identity.IdentityProxy;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authenticated user surface. Pure pass-through — the BFF carries the
 * caller's Authorization header into identity-service and bubbles the
 * response back. Upstream errors are translated by
 * {@link app.viaverse.webbff.identity.IdentityProxyExceptionHandler}.
 */
@RestController
@RequestMapping("/api/me")
public class BffMeController {

    private static final String IDENTITY_PATH = "/api/v1/me";

    private final IdentityProxy identityProxy;

    public BffMeController(IdentityProxy identityProxy) {
        this.identityProxy = identityProxy;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        IdentityProxy.ProxyResponse proxied = identityProxy.get(IDENTITY_PATH, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/password", body, authorization);
    }

    @PostMapping("/2fa/enroll")
    public ResponseEntity<Map<String, Object>> enroll2fa(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/2fa/enroll", Map.of(), authorization);
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<Map<String, Object>> confirm2fa(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/2fa/confirm", body, authorization);
    }

    @DeleteMapping("/2fa")
    public ResponseEntity<Map<String, Object>> disable2fa(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return forward(HttpMethod.DELETE, IDENTITY_PATH + "/2fa", body, authorization);
    }

    private ResponseEntity<Map<String, Object>> forward(
            HttpMethod method, String path, Object body, String authorization
    ) {
        IdentityProxy.ProxyResponse proxied = identityProxy.exchange(method, path, body, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }
}
