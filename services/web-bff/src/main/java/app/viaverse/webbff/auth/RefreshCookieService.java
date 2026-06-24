package app.viaverse.webbff.auth;

import app.viaverse.webbff.config.BffProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Read / write the refresh-token cookie. HttpOnly + Secure + SameSite=Lax
 * means an XSS payload can't read it, a top-level navigation can still
 * send it (so OAuth redirects work), and a non-Secure cookie is rejected
 * in production by the browser. Path is scoped to {@code /api/auth} so
 * the cookie is sent only to the endpoints that actually need it.
 */
@Component
public class RefreshCookieService {

    private final BffProperties properties;

    public RefreshCookieService(BffProperties properties) {
        this.properties = properties;
    }

    public Optional<String> read(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(properties.getRefreshCookie().getName())) {
                String value = cookie.getValue();
                if (value == null || value.isBlank()) {
                    return Optional.empty();
                }
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public void set(HttpServletResponse response, String refreshToken) {
        BffProperties.RefreshCookie cfg = properties.getRefreshCookie();
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cfg.getName(), refreshToken)
                .httpOnly(true)
                .secure(cfg.isSecure())
                .path(cfg.getPath())
                .sameSite(cfg.getSameSite())
                .maxAge(Duration.ofDays(cfg.getMaxAgeDays()));
        if (cfg.getDomain() != null && !cfg.getDomain().isBlank()) {
            builder.domain(cfg.getDomain());
        }
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    public void clear(HttpServletResponse response) {
        BffProperties.RefreshCookie cfg = properties.getRefreshCookie();
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cfg.getName(), "")
                .httpOnly(true)
                .secure(cfg.isSecure())
                .path(cfg.getPath())
                .sameSite(cfg.getSameSite())
                .maxAge(Duration.ZERO);
        if (cfg.getDomain() != null && !cfg.getDomain().isBlank()) {
            builder.domain(cfg.getDomain());
        }
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }
}
