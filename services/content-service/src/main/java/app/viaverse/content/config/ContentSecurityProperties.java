package app.viaverse.content.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.content.security.jwt")
public class ContentSecurityProperties {
    private String secret;
    private List<String> previousSecrets = new ArrayList<>();

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public List<String> getPreviousSecrets() { return previousSecrets; }
    public void setPreviousSecrets(List<String> previousSecrets) {
        this.previousSecrets = previousSecrets == null ? new ArrayList<>() : new ArrayList<>(previousSecrets);
    }
}
