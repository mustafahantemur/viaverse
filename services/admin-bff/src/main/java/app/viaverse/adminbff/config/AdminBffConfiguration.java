package app.viaverse.adminbff.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(AdminBffProperties.class)
public class AdminBffConfiguration {

    @Bean
    RestClient adminProfileRestClient(AdminBffProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getProfileBaseUrl())
                .build();
    }

    @Bean
    WebMvcConfigurer adminCorsConfigurer(AdminBffProperties properties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origins = properties.getCors().getAllowedOrigins().split(",");
                registry.addMapping("/api/admin/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST")
                        .allowedHeaders("*");
            }
        };
    }
}
