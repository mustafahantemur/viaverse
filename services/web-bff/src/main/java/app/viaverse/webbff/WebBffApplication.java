package app.viaverse.webbff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Backend-for-Frontend for the public-facing web (web-next) and mobile
 * (mobile-kmp) clients. Sits in front of identity-service (and, later,
 * other Viaverse services) and:
 *
 * <ul>
 *   <li>Hides the internal microservice topology from clients.</li>
 *   <li>Translates raw service responses into client-friendly shapes
 *       (currently just pass-through; aggregations land here as the
 *       product grows).</li>
 *   <li>Owns the web session model: refresh tokens travel as
 *       HttpOnly/Secure cookies so an XSS payload cannot exfiltrate
 *       them; mobile keeps using Bearer tokens (no XSS surface) and
 *       receives the refresh token in the response body.</li>
 *   <li>Centralises CORS for the SPA origin allowlist.</li>
 * </ul>
 */
@SpringBootApplication(scanBasePackages = "app.viaverse")
public class WebBffApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebBffApplication.class, args);
    }
}
