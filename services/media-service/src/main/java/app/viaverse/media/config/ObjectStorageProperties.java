package app.viaverse.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "object-storage")
public class ObjectStorageProperties {
    private String provider = "seaweedfs";
    private String endpoint = "http://localhost:8333";
    private String region = "local";
    private String accessKey = "viaverse";
    private String secretKey = "viaverse-local-secret";
    private boolean pathStyleAccess = true;
    private final Buckets buckets = new Buckets();

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public boolean isPathStyleAccess() { return pathStyleAccess; }
    public void setPathStyleAccess(boolean pathStyleAccess) { this.pathStyleAccess = pathStyleAccess; }
    public Buckets getBuckets() { return buckets; }

    public static class Buckets {
        private String media = "viaverse-media-local";
        public String getMedia() { return media; }
        public void setMedia(String media) { this.media = media; }
    }
}
