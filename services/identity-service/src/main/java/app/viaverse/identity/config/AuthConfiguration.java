package app.viaverse.identity.config;

import app.viaverse.identity.auth.infrastructure.security.JwtAccessTokenService;
import app.viaverse.identity.auth.infrastructure.security.IdentityJwtValidator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.auth.infrastructure.security.RotatingJwtDecoder;
import app.viaverse.identity.auth.domain.enums.OtpDeliveryProviderEnum;
import app.viaverse.identity.auth.domain.enums.SmsProviderEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthConfiguration.class);
    private final AuthProperties properties;
    private final HttpProperties httpProperties;
    private final Environment environment;

    public AuthConfiguration(
            AuthProperties properties,
            HttpProperties httpProperties,
            Environment environment
    ) {
        this.properties = properties;
        this.httpProperties = httpProperties;
        this.environment = environment;
    }

    @PostConstruct
    void validateConfiguration() {
        validate(properties, environment.getActiveProfiles());
        warnIfTrustedProxiesEmptyInNonLocalProfile(httpProperties, environment.getActiveProfiles());
    }

    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    TokenHasher tokenHasher(AuthProperties properties) {
        return new TokenHasher(properties.getJwt().getSecret());
    }

    @Bean
    SecretKey identityJwtSecretKey(AuthProperties properties) {
        return new SecretKeySpec(properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey identityJwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(identityJwtSecretKey));
    }

    @Bean
    JwtDecoder jwtDecoder(AuthProperties properties) {
        List<JwtDecoder> decoders = new ArrayList<>();
        decoders.add(buildDecoder(properties.getJwt().getSecret()));
        for (String previousSecret : properties.getJwt().getPreviousSecrets()) {
            if (!isBlank(previousSecret)) {
                decoders.add(buildDecoder(previousSecret));
            }
        }
        return new RotatingJwtDecoder(decoders);
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

    @Bean
    JwtAccessTokenService jwtAccessTokenService(AuthProperties properties, JwtEncoder jwtEncoder) {
        return new JwtAccessTokenService(jwtEncoder, properties.getJwt().getAccessTokenTtl());
    }

    private static void warnIfTrustedProxiesEmptyInNonLocalProfile(
            HttpProperties httpProperties,
            String[] activeProfiles
    ) {
        if (httpProperties.getTrustedProxies().isEmpty() && !hasLocalOrTestProfile(activeProfiles)) {
            LOGGER.warn(
                    "viaverse.http.trusted-proxies is empty in a non-local profile. "
                            + "ClientIpResolver will report the direct peer (typically the load balancer) "
                            + "as the client IP. Set VIAVERSE_HTTP_TRUSTED_PROXIES to the upstream proxy "
                            + "CIDR (e.g. the ALB / CloudFront range) before going to production."
            );
        }
    }

    public static void validate(AuthProperties properties, String[] activeProfiles) {
        if (properties.getJwt().getSecret() == null || properties.getJwt().getSecret().isBlank()) {
            throw IdentityErrors.jwtSecretRequired();
        }
        if (properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8).length < 32) {
            throw IdentityErrors.jwtSecretTooWeak();
        }
        for (String previousSecret : properties.getJwt().getPreviousSecrets()) {
            if (isBlank(previousSecret) || previousSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
                throw IdentityErrors.jwtSecretTooWeak();
            }
        }
        if (properties.getDebug().isEnabled() && !hasLocalOrTestProfile(activeProfiles)) {
            throw IdentityErrors.debugOtpProfileInvalid();
        }
        if (properties.getDebug().isSeedTestUsers() && !hasLocalOrTestProfile(activeProfiles)) {
            throw IdentityErrors.debugSeedUsersProfileInvalid();
        }
        if (properties.getDebug().isEnabled()
                && (properties.getDebug().getFixedOtp() == null || properties.getDebug().getFixedOtp().isBlank())) {
            throw IdentityErrors.debugOtpFixedValueRequired();
        }
        if (properties.getOtp().getDelivery().getProvider() == OtpDeliveryProviderEnum.SMS) {
            if (properties.getSms().getProvider() != SmsProviderEnum.NETGSM) {
                throw IdentityErrors.smsProviderDisabled();
            }
            AuthProperties.Netgsm netgsm = properties.getSms().getNetgsm();
            if (isBlank(netgsm.getEndpoint())
                    || isBlank(netgsm.getUsername())
                    || isBlank(netgsm.getPassword())
                    || isBlank(netgsm.getHeader())
                    || isBlank(netgsm.getMessageTemplate())) {
                throw IdentityErrors.netgsmConfigurationInvalid();
            }
            if (countOccurrences(netgsm.getMessageTemplate(), "%s") != 1) {
                throw IdentityErrors.netgsmConfigurationInvalid();
            }
        }
        if (properties.getSocial().getGoogle().isEnabled()
                && isBlank(properties.getSocial().getGoogle().getClientId())) {
            throw IdentityErrors.socialProviderConfigurationInvalid("Google");
        }
        if (properties.getSocial().getApple().isEnabled()
                && isBlank(properties.getSocial().getApple().getClientId())) {
            throw IdentityErrors.socialProviderConfigurationInvalid("Apple");
        }
    }

    private static boolean hasLocalOrTestProfile(String[] activeProfiles) {
        return Arrays.stream(activeProfiles).anyMatch(profile -> profile.equals("local") || profile.equals("test"));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        int index = 0;
        while ((index = haystack.indexOf(needle, index)) != -1) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
