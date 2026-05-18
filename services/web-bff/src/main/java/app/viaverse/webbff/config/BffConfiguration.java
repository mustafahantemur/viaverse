package app.viaverse.webbff.config;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(BffProperties.class)
public class BffConfiguration {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

    @Bean
    RestClient identityRestClient(BffProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(READ_TIMEOUT);
        return RestClient.builder()
                .baseUrl(properties.getIdentityBaseUrl())
                .requestFactory(factory)
                .build();
    }

    @Bean
    RestClient profileRestClient(BffProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(READ_TIMEOUT);
        return RestClient.builder()
                .baseUrl(properties.getProfileBaseUrl())
                .requestFactory(factory)
                .build();
    }

    @Bean
    CorsConfigurationSource bffCorsConfigurationSource(BffProperties properties) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(properties.getCors().getAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Client-Fingerprint"));
        config.setExposedHeaders(List.of("Retry-After"));
        // Cookies (refresh token) must travel cross-origin, so credentials are on.
        // Allowed-origins MUST be a real list (no "*") for credentials to work.
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
