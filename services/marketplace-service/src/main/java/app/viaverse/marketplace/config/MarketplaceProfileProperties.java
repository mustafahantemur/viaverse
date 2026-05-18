package app.viaverse.marketplace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.marketplace.profile")
public class MarketplaceProfileProperties {

    private String baseUrl = "http://localhost:8111";
    private String internalApiToken = "local-dev-internal-token-change-me";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getInternalApiToken() {
        return internalApiToken;
    }

    public void setInternalApiToken(String internalApiToken) {
        this.internalApiToken = internalApiToken;
    }
}
