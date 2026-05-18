package app.viaverse.adminbff.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AdminBffProperties.class)
public class AdminBffConfiguration {

    @Bean
    RestClient adminProfileRestClient(AdminBffProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getProfileBaseUrl())
                .build();
    }
}
