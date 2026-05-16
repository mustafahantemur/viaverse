package app.viaverse.identity.auth.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class RotatingJwtDecoderTest {
    private static final String CURRENT_SECRET = "current-jwt-secret-that-is-long-enough";
    private static final String PREVIOUS_SECRET = "previous-jwt-secret-that-is-long-enough";

    @Test
    void acceptsTokensSignedByPreviousSecretDuringRotationWindow() {
        JwtAccessTokenService previousIssuer = new JwtAccessTokenService(
                encoder(PREVIOUS_SECRET),
                java.time.Duration.ofMinutes(15)
        );
        String token = previousIssuer.issue(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(app.viaverse.identity.account.domain.AccountRoleEnum.USER),
                Instant.now()
        );

        RotatingJwtDecoder decoder = new RotatingJwtDecoder(List.of(
                decoder(CURRENT_SECRET),
                decoder(PREVIOUS_SECRET)
        ));

        assertThat(decoder.decode(token).getClaimAsString("iss")).isEqualTo(IdentityJwtClaims.ISSUER);
    }

    private static NimbusJwtEncoder encoder(String secret) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key(secret)));
    }

    private static JwtDecoder decoder(String secret) {
        return NimbusJwtDecoder.withSecretKey(key(secret))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private static SecretKeySpec key(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
