package app.viaverse.webbff.auth;

import app.viaverse.webbff.identity.IdentityProxy;
import app.viaverse.webbff.identity.IdentityProxyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public-facing auth surface. One per identity-service endpoint, but the
 * BFF layer is where we:
 *
 * <ul>
 *   <li>Promote {@code refreshToken} from response body to an HttpOnly
 *       cookie on every credential-issuing endpoint. Mobile clients still
 *       see it in the body; web clients should ignore the body field
 *       and rely on the cookie.</li>
 *   <li>Read the refresh token from the cookie on {@code /refresh} so the
 *       browser never has to put it in JS.</li>
 *   <li>Clear the cookie on logout.</li>
 * </ul>
 *
 * <p>Everything else is a transparent pass-through; identity is still the
 * source of truth for validation, rate-limit, and error shape.
 */
@RestController
@RequestMapping("/api/auth")
public class BffAuthController {

    private static final String IDENTITY_PATH = "/api/v1/auth";

    private final IdentityProxy identityProxy;
    private final RefreshCookieService refreshCookieService;

    public BffAuthController(IdentityProxy identityProxy, RefreshCookieService refreshCookieService) {
        this.identityProxy = identityProxy;
        this.refreshCookieService = refreshCookieService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start(@RequestBody Map<String, Object> body) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/start", body, null);
    }

    @PostMapping("/password-login")
    public ResponseEntity<Map<String, Object>> passwordLogin(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndMaybeSetCookie(HttpMethod.POST, IDENTITY_PATH + "/password-login", body, null, response);
    }

    @PostMapping("/verify-totp")
    public ResponseEntity<Map<String, Object>> verifyTotp(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndMaybeSetCookie(HttpMethod.POST, IDENTITY_PATH + "/verify-totp", body, null, response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, Object> body) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/verify-otp", body, null);
    }

    @PostMapping("/social/{provider}")
    public ResponseEntity<Map<String, Object>> social(
            @PathVariable String provider,
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndMaybeSetCookie(
                HttpMethod.POST, IDENTITY_PATH + "/social/" + provider, body, null, response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndMaybeSetCookie(HttpMethod.POST, IDENTITY_PATH + "/register", body, null, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @RequestBody(required = false) Map<String, Object> body,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // Prefer cookie (web) — fall back to body (mobile).
        String cookieToken = refreshCookieService.read(request).orElse(null);
        Map<String, Object> forwarded;
        if (cookieToken != null) {
            forwarded = new LinkedHashMap<>();
            forwarded.put("refreshToken", cookieToken);
        } else {
            forwarded = body == null ? Map.of() : body;
        }
        return forwardAndMaybeSetCookie(
                HttpMethod.POST, IDENTITY_PATH + "/refresh", forwarded, null, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String cookieToken = refreshCookieService.read(request).orElse(null);
        Map<String, Object> forwarded = new LinkedHashMap<>();
        if (body != null) {
            forwarded.putAll(body);
        }
        if (cookieToken != null && !forwarded.containsKey("refreshToken")) {
            forwarded.put("refreshToken", cookieToken);
        }
        ResponseEntity<Map<String, Object>> result =
                forward(HttpMethod.POST, IDENTITY_PATH + "/logout", forwarded, authorization);
        refreshCookieService.clear(response);
        return result;
    }

    @GetMapping("/required-consents")
    public ResponseEntity<Map<String, Object>> requiredConsents() {
        IdentityProxy.ProxyResponse proxied = identityProxy.get(IDENTITY_PATH + "/required-consents", null);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    // ---- Forgot password ----

    @PostMapping("/forgot-password/start")
    public ResponseEntity<Map<String, Object>> forgotPasswordStart(@RequestBody Map<String, Object> body) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/forgot-password/start", body, null);
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<Map<String, Object>> forgotPasswordVerifyOtp(@RequestBody Map<String, Object> body) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/forgot-password/verify-otp", body, null);
    }

    @PostMapping("/forgot-password/complete")
    public ResponseEntity<Map<String, Object>> forgotPasswordComplete(@RequestBody Map<String, Object> body) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/forgot-password/complete", body, null);
    }

    // ---- helpers ----

    private ResponseEntity<Map<String, Object>> forward(
            HttpMethod method,
            String path,
            Object body,
            String authorization
    ) {
        IdentityProxy.ProxyResponse proxied = identityProxy.exchange(method, path, body, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    private ResponseEntity<Map<String, Object>> forwardAndMaybeSetCookie(
            HttpMethod method,
            String path,
            Object body,
            String authorization,
            HttpServletResponse response
    ) {
        IdentityProxy.ProxyResponse proxied = identityProxy.exchange(method, path, body, authorization);
        Map<String, Object> data = extractData(proxied.body());
        if (data != null) {
            Object refreshToken = data.get("refreshToken");
            if (refreshToken instanceof String token && !token.isBlank()) {
                refreshCookieService.set(response, token);
            }
        }
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractData(Map<String, Object> body) {
        if (body == null) {
            return null;
        }
        Object data = body.get("data");
        if (data instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return body;
    }

    @ExceptionHandler(IdentityProxyException.class)
    ResponseEntity<Map<String, Object>> handleUpstream(IdentityProxyException exception) {
        return ResponseEntity.status(exception.getStatus()).body(exception.getBody());
    }
}
