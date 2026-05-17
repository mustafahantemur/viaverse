package app.viaverse.webbff.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.bff")
public class BffProperties {
    private String identityBaseUrl = "http://localhost:8101";
    private final RefreshCookie refreshCookie = new RefreshCookie();
    private final Cors cors = new Cors();

    public String getIdentityBaseUrl() { return identityBaseUrl; }
    public void setIdentityBaseUrl(String identityBaseUrl) { this.identityBaseUrl = identityBaseUrl; }
    public RefreshCookie getRefreshCookie() { return refreshCookie; }
    public Cors getCors() { return cors; }

    public static class RefreshCookie {
        private String name = "viaverse_rt";
        private String domain = "";
        private String path = "/api/auth";
        private boolean secure = false;
        private String sameSite = "Lax";
        private int maxAgeDays = 30;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public boolean isSecure() { return secure; }
        public void setSecure(boolean secure) { this.secure = secure; }
        public String getSameSite() { return sameSite; }
        public void setSameSite(String sameSite) { this.sameSite = sameSite; }
        public int getMaxAgeDays() { return maxAgeDays; }
        public void setMaxAgeDays(int maxAgeDays) { this.maxAgeDays = maxAgeDays; }
    }

    public static class Cors {
        private List<String> allowedOrigins = List.of();

        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }
}
