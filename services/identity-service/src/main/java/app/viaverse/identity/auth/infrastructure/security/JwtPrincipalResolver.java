package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.security.identity.IdentityJwtClaims;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public final class JwtPrincipalResolver {
    public JwtPrincipal resolve(Jwt jwt) {
        if (jwt == null) {
            throw IdentityErrors.bearerTokenRequired();
        }
        try {
            return new JwtPrincipal(
                    UUID.fromString(jwt.getSubject()),
                    UUID.fromString(jwt.getClaimAsString(IdentityJwtClaims.SESSION_ID))
            );
        } catch (RuntimeException exception) {
            throw IdentityErrors.invalidAccessToken();
        }
    }
}
