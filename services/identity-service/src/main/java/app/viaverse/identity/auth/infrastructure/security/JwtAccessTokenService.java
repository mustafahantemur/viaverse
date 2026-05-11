package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.identity.shared.error.IdentityErrors;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

public class JwtAccessTokenService {
    private static final String ISSUER = "viaverse-identity";
    private static final String SESSION_ID_CLAIM = "sid";

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final Duration accessTokenTtl;

    public JwtAccessTokenService(String secret, Duration accessTokenTtl) {
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        NimbusJwtDecoder nimbusDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        nimbusDecoder.setJwtValidator(jwt -> OAuth2TokenValidatorResult.success());
        this.decoder = nimbusDecoder;
        this.accessTokenTtl = accessTokenTtl;
    }

    public String issue(UUID accountId, UUID sessionId, Instant now) {
        Instant expiresAt = now.plus(accessTokenTtl);
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(accountId.toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim(SESSION_ID_CLAIM, sessionId.toString())
                .build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public JwtPrincipal verify(String token, Instant now) {
        Jwt jwt = decode(token);
        if (!ISSUER.equals(jwt.getClaimAsString("iss"))) {
            throw IdentityErrors.invalidAccessToken();
        }
        if (jwt.getExpiresAt() == null || jwt.getExpiresAt().isBefore(now)) {
            throw IdentityErrors.accessTokenExpired();
        }
        try {
            return new JwtPrincipal(
                    UUID.fromString(jwt.getSubject()),
                    UUID.fromString(jwt.getClaimAsString(SESSION_ID_CLAIM))
            );
        } catch (RuntimeException exception) {
            throw IdentityErrors.invalidAccessToken();
        }
    }

    public long expiresInSeconds() {
        return accessTokenTtl.toSeconds();
    }

    private Jwt decode(String token) {
        try {
            return decoder.decode(token);
        } catch (BadJwtException exception) {
            throw IdentityErrors.invalidAccessToken();
        } catch (JwtException exception) {
            throw IdentityErrors.invalidAccessToken();
        }
    }
}
