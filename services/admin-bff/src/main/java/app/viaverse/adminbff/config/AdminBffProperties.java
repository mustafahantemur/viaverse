package app.viaverse.adminbff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.admin-bff")
public class AdminBffProperties {

    private String profileBaseUrl = "http://localhost:8111";
    private String internalApiToken = "local-dev-internal-token-change-me";
    private Cors cors = new Cors();

    public String getProfileBaseUrl() {
        return profileBaseUrl;
    }

    public void setProfileBaseUrl(String profileBaseUrl) {
        this.profileBaseUrl = profileBaseUrl;
    }

    public String getInternalApiToken() {
        return internalApiToken;
    }

    public void setInternalApiToken(String internalApiToken) {
        this.internalApiToken = internalApiToken;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:3001";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
