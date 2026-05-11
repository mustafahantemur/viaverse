package app.viaverse.identity.config;

import app.viaverse.identity.auth.infrastructure.security.JwtAccessTokenService;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.auth.domain.enums.OtpDeliveryProvider;
import app.viaverse.shared.kernel.error.TechnicalException;
import java.security.SecureRandom;
import java.util.Arrays;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
    JwtAccessTokenService jwtAccessTokenService(AuthProperties properties) {
        return new JwtAccessTokenService(properties.getJwt().getSecret(), properties.getJwt().getAccessTokenTtl());
    }

    @Bean
    ApplicationRunner authConfigurationValidator(AuthProperties properties, Environment environment) {
        return args -> validate(properties, environment.getActiveProfiles());
    }

    public static void validate(AuthProperties properties, String[] activeProfiles) {
        if (properties.getJwt().getSecret() == null || properties.getJwt().getSecret().isBlank()) {
            throw new TechnicalException("Identity JWT secret must be configured");
        }
        if (properties.getDebug().isEnabled() && !hasLocalOrTestProfile(activeProfiles)) {
            throw new TechnicalException("Debug OTP can only be enabled in local or test profiles");
        }
        if (properties.getDebug().isSeedTestUsers() && !hasLocalOrTestProfile(activeProfiles)) {
            throw new TechnicalException("Debug seed users can only be enabled in local or test profiles");
        }
        if (properties.getDebug().isEnabled()
                && (properties.getDebug().getFixedOtp() == null || properties.getDebug().getFixedOtp().isBlank())) {
            throw new TechnicalException("Debug OTP is enabled but no fixed OTP is configured");
        }
        if (properties.getOtp().getDelivery().getProvider() == OtpDeliveryProvider.SMS) {
            throw new TechnicalException("SMS OTP delivery is not implemented yet");
        }
    }

    private static boolean hasLocalOrTestProfile(String[] activeProfiles) {
        return Arrays.stream(activeProfiles).anyMatch(profile -> profile.equals("local") || profile.equals("test"));
    }
}
