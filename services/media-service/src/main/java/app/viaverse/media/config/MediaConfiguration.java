package app.viaverse.media.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        MediaSecurityProperties.class,
        MediaProperties.class,
        ObjectStorageProperties.class
})
public class MediaConfiguration {
}
