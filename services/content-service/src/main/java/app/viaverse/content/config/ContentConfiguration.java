package app.viaverse.content.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ContentSecurityProperties.class)
public class ContentConfiguration {
}
