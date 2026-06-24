package app.viaverse.profile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.profile.internal")
public class ProfileInternalProperties {

    private String apiToken = "local-dev-internal-token-change-me";

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
