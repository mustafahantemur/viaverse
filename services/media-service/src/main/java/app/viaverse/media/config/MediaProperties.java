package app.viaverse.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.media")
public class MediaProperties {
    private int uploadSessionTtlMinutes = 15;

    public int getUploadSessionTtlMinutes() { return uploadSessionTtlMinutes; }
    public void setUploadSessionTtlMinutes(int uploadSessionTtlMinutes) {
        this.uploadSessionTtlMinutes = uploadSessionTtlMinutes;
    }
}
