package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.security.oauth2.jwt.JwsHeader;

public class JwtAccessTokenService {
    private final JwtEncoder encoder;
    private final Duration accessTokenTtl;

    public JwtAccessTokenService(JwtEncoder encoder, Duration accessTokenTtl) {
        this.encoder = encoder;
        this.accessTokenTtl = accessTokenTtl;
    }

    public String issue(UUID accountId, UUID sessionId, Instant now) {
        Instant expiresAt = now.plus(accessTokenTtl);
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(IdentityJwtClaims.ISSUER)
                .subject(accountId.toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim(IdentityJwtClaims.SESSION_ID, sessionId.toString())
                .build();
        try {
            return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        } catch (JwtEncodingException exception) {
            throw IdentityErrors.jwtEncodingFailed(exception);
        }
    }

    public long expiresInSeconds() {
        return accessTokenTtl.toSeconds();
    }
}
