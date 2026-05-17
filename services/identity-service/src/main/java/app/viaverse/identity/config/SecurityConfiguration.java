package app.viaverse.identity.config;

import app.viaverse.identity.auth.infrastructure.security.IdentityAuthenticationEntryPoint;
import app.viaverse.identity.auth.infrastructure.security.IdentityJwtClaims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    @Order(1)
    SecurityFilterChain identityApiDocsSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/scalar", "/scalar/**", "/v3/api-docs", "/v3/api-docs/**", "/favicon.svg")
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(Customizer.withDefaults())
                        .referrerPolicy(policy -> policy.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; script-src 'self' 'unsafe-inline'; "
                                        + "style-src 'self' 'unsafe-inline'; img-src 'self' data:; "
                                        + "font-src 'self' data:; connect-src 'self'; frame-ancestors 'none'"))
                        .permissionsPolicyHeader(p -> p.policy(
                                "accelerometer=(), camera=(), geolocation=(), gyroscope=(), "
                                        + "magnetometer=(), microphone=(), payment=(), usb=()"))
                )
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain identitySecurityFilterChain(
            HttpSecurity http,
            IdentityAuthenticationEntryPoint authenticationEntryPoint,
            @Qualifier("identityCorsConfigurationSource") CorsConfigurationSource corsConfigurationSource
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
                        // This is a JSON API, never a UI surface. Lock everything down so
                        // a misconfigured client can't load anything in a browser context.
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'none'; frame-ancestors 'none'"))
                        .permissionsPolicyHeader(p -> p.policy(
                                "accelerometer=(), camera=(), geolocation=(), gyroscope=(), "
                                        + "magnetometer=(), microphone=(), payment=(), usb=()"))
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/start").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/password-login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/verify-totp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/verify-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register-admin").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/social/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/forgot-password/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/required-consents").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/scalar", "/scalar/**", "/favicon.svg")
                        .permitAll()
                        .requestMatchers("/api/v1/me", "/api/v1/me/**").authenticated()
                        .requestMatchers("/api/v1/admin", "/api/v1/admin/**").authenticated()
                        .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(identityBearerTokenResolver())
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .build();
    }

    @Bean
    BearerTokenResolver identityBearerTokenResolver() {
        DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
        return request -> isBootstrapAuthEndpoint(request) ? null : delegate.resolve(request);
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

    /**
     * CORS configuration driven by {@code viaverse.http.cors.allowed-origins}.
     * Default is empty — meaning no cross-origin request is allowed. Add explicit
     * origin URLs per environment (e.g. {@code https://app.viaverse.com}).
     */
    @Bean
    CorsConfigurationSource identityCorsConfigurationSource(HttpProperties properties) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(properties.getCors().getAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Client-Fingerprint"));
        config.setExposedHeaders(List.of("Retry-After"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private boolean isBootstrapAuthEndpoint(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return "/api/v1/auth/start".equals(path)
                || "/api/v1/auth/password-login".equals(path)
                || "/api/v1/auth/verify-totp".equals(path)
                || "/api/v1/auth/verify-otp".equals(path)
                || "/api/v1/auth/register".equals(path)
                || path.startsWith("/api/v1/auth/register/")
                || "/api/v1/auth/register-admin".equals(path)
                || "/api/v1/auth/refresh".equals(path)
                || path.startsWith("/api/v1/auth/social/")
                || path.startsWith("/api/v1/auth/forgot-password/");
    }
}
