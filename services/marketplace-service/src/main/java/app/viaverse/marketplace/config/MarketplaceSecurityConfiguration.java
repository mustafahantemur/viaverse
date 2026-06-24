package app.viaverse.marketplace.config;

import app.viaverse.security.identity.IdentityJwtClaims;
import app.viaverse.security.identity.IdentityJwtValidator;
import app.viaverse.security.identity.RotatingJwtDecoder;
import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.TechnicalException;
import app.viaverse.web.http.HttpProperties;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class MarketplaceSecurityConfiguration {

    private final MarketplaceSecurityProperties properties;

    public MarketplaceSecurityConfiguration(MarketplaceSecurityProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void validate() {
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            throw new TechnicalException(
                    AppErrorCode.AUTH_PROVIDER_CONFIGURATION_INVALID,
                    "Marketplace JWT secret is required"
            );
        }
        if (properties.getSecret().getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new TechnicalException(
                    AppErrorCode.AUTH_PROVIDER_CONFIGURATION_INVALID,
                    "Marketplace JWT secret must be at least 32 bytes"
            );
        }
    }

    @Bean
    SecurityFilterChain marketplaceSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("marketplaceCorsConfigurationSource") CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(Customizer.withDefaults())
                        .referrerPolicy(policy -> policy.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000L))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'none'; frame-ancestors 'none'"))
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/health",
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/prometheus",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/scalar",
                                "/scalar/**",
                                "/favicon.svg"
                        )
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/requests/open").authenticated()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter()
                )))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        List<JwtDecoder> decoders = new ArrayList<>();
        decoders.add(buildDecoder(properties.getSecret()));
        for (String previousSecret : properties.getPreviousSecrets()) {
            if (previousSecret != null && !previousSecret.isBlank()) {
                decoders.add(buildDecoder(previousSecret));
            }
        }
        return new RotatingJwtDecoder(decoders);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName(IdentityJwtClaims.ROLES);
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    CorsConfigurationSource marketplaceCorsConfigurationSource(HttpProperties properties) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(properties.getCors().getAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private JwtDecoder buildDecoder(String secret) {
        Duration clockSkew = Duration.ofSeconds(60);
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(clockSkew),
                new IdentityJwtValidator(clockSkew)
        );
        decoder.setJwtValidator(validator);
        return decoder;
    }
}
