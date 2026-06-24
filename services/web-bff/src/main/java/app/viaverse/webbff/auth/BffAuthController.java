package app.viaverse.webbff.auth;

import app.viaverse.webbff.identity.IdentityProxy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
 *       see it in the body; web clients should ignore the body field and
 *       rely on the cookie.</li>
 *   <li>Read the refresh token from the cookie on {@code /refresh} so the
 *       browser never has to put it in JS.</li>
 *   <li>Clear the cookie on logout.</li>
 * </ul>
 *
 * <p>Everything else is a transparent pass-through; identity is still the
 * source of truth for validation, rate-limit, and error shape. Upstream
 * errors are translated by {@link app.viaverse.webbff.identity.IdentityProxyExceptionHandler}.
 */
@RestController
@RequestMapping("/api/auth")
public class BffAuthController {

    private static final String IDENTITY_PATH = "/api/v1/auth";

    private final IdentityProxy identityProxy;
    private final RefreshCookieService refreshCookieService;
    private final ApiResponseEnvelope envelope;

    public BffAuthController(
            IdentityProxy identityProxy,
            RefreshCookieService refreshCookieService,
            ApiResponseEnvelope envelope
    ) {
        this.identityProxy = identityProxy;
        this.refreshCookieService = refreshCookieService;
        this.envelope = envelope;
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
        return forwardAndPromoteRefreshCookie(
                HttpMethod.POST, IDENTITY_PATH + "/password-login", body, null, response);
    }

    @PostMapping("/verify-totp")
    public ResponseEntity<Map<String, Object>> verifyTotp(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndPromoteRefreshCookie(
                HttpMethod.POST, IDENTITY_PATH + "/verify-totp", body, null, response);
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
        return forwardAndPromoteRefreshCookie(
                HttpMethod.POST, IDENTITY_PATH + "/social/" + provider, body, null, response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndPromoteRefreshCookie(
                HttpMethod.POST, IDENTITY_PATH + "/register", body, null, response);
    }

    // ---- Draft-based registration (form-first) ----

    @PostMapping("/register/start")
    public ResponseEntity<Map<String, Object>> registerStart(@RequestBody Map<String, Object> body) {
        return forward(HttpMethod.POST, IDENTITY_PATH + "/register/start", body, null);
    }

    @PostMapping("/register/verify-email")
    public ResponseEntity<Map<String, Object>> registerVerifyEmail(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndPromoteRefreshCookie(
                HttpMethod.POST, IDENTITY_PATH + "/register/verify-email", body, null, response);
    }

    @PostMapping("/register/verify-phone")
    public ResponseEntity<Map<String, Object>> registerVerifyPhone(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response
    ) {
        return forwardAndPromoteRefreshCookie(
                HttpMethod.POST, IDENTITY_PATH + "/register/verify-phone", body, null, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @RequestBody(required = false) Map<String, Object> body,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // Prefer cookie (web) — fall back to body (mobile). Identity-service
        // only sees one shape: {refreshToken: …}.
        Map<String, Object> forwarded = refreshCookieService.read(request)
                .map(token -> {
                    Map<String, Object> body0 = new LinkedHashMap<>();
                    body0.put("refreshToken", token);
                    return body0;
                })
                .orElse(body == null ? Map.of() : body);
        return forwardAndPromoteRefreshCookie(
                HttpMethod.POST, IDENTITY_PATH + "/refresh", forwarded, null, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Map<String, Object> forwarded = new LinkedHashMap<>();
        if (body != null) forwarded.putAll(body);
        refreshCookieService.read(request)
                .filter(token -> !forwarded.containsKey("refreshToken"))
                .ifPresent(token -> forwarded.put("refreshToken", token));
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

    @GetMapping("/capability-terms")
    public ResponseEntity<Map<String, Object>> capabilityTerms() {
        IdentityProxy.ProxyResponse proxied = identityProxy.get(IDENTITY_PATH + "/capability-terms", null);
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

    // ---- shared forwarder ----

    private ResponseEntity<Map<String, Object>> forward(
            HttpMethod method, String path, Object body, String authorization
    ) {
        IdentityProxy.ProxyResponse proxied = identityProxy.exchange(method, path, body, authorization);
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }

    private ResponseEntity<Map<String, Object>> forwardAndPromoteRefreshCookie(
            HttpMethod method,
            String path,
            Object body,
            String authorization,
            HttpServletResponse response
    ) {
        IdentityProxy.ProxyResponse proxied = identityProxy.exchange(method, path, body, authorization);
        envelope.stringField(proxied.body(), "refreshToken")
                .ifPresent(token -> refreshCookieService.set(response, token));
        return ResponseEntity.status(proxied.status()).body(proxied.body());
    }
}
