package app.viaverse.identity.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.http")
public class HttpProperties {
    private final List<String> trustedProxies = new ArrayList<>();
    private final Cors cors = new Cors();

    public List<String> getTrustedProxies() {
        return trustedProxies;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Cors {
        private final List<String> allowedOrigins = new ArrayList<>();

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }
    }
}
