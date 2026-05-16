package app.viaverse.identity.config;

import app.viaverse.identity.auth.infrastructure.security.JwtAccessTokenService;
import app.viaverse.identity.auth.infrastructure.security.IdentityJwtValidator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.auth.domain.enums.OtpDeliveryProviderEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.ApplicationRunner;
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
    JwtDecoder jwtDecoder(SecretKey identityJwtSecretKey) {
        Duration clockSkew = Duration.ofSeconds(60);
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(identityJwtSecretKey)
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

    @Bean
    ApplicationRunner authConfigurationValidator(AuthProperties properties, Environment environment) {
        return args -> validate(properties, environment.getActiveProfiles());
    }

    public static void validate(AuthProperties properties, String[] activeProfiles) {
        if (properties.getJwt().getSecret() == null || properties.getJwt().getSecret().isBlank()) {
            throw IdentityErrors.jwtSecretRequired();
        }
        if (properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8).length < 32) {
            throw IdentityErrors.jwtSecretTooWeak();
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
            throw IdentityErrors.smsProviderDisabled();
        }
    }

    private static boolean hasLocalOrTestProfile(String[] activeProfiles) {
        return Arrays.stream(activeProfiles).anyMatch(profile -> profile.equals("local") || profile.equals("test"));
    }
}
