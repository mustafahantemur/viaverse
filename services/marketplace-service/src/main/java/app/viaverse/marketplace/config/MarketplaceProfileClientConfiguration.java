package app.viaverse.marketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MarketplaceProfileClientConfiguration {

    @Bean
    RestClient profileInternalRestClient(MarketplaceProfileProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
