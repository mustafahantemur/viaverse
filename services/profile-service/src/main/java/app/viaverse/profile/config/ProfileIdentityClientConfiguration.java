package app.viaverse.profile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProfileIdentityClientConfiguration {

    @Bean
    RestClient identityInternalRestClient(ProfileIdentityProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
