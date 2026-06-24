package app.viaverse.profile.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ProfileProperties.class,
        ProfileSecurityProperties.class,
        ProfileIdentityProperties.class,
        ProfileInternalProperties.class
})
public class ProfileConfiguration {
}
