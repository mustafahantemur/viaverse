package app.viaverse.marketplace.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        MarketplaceProfileProperties.class,
        MarketplaceSecurityProperties.class
})
public class MarketplaceConfiguration {
}
