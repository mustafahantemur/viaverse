package app.viaverse.identity.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.http")
public class HttpProperties {
    private final List<String> trustedProxies = new ArrayList<>();

    public List<String> getTrustedProxies() {
        return trustedProxies;
    }
}
