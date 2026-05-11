package app.viaverse.identity.auth.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public final class IdentityJwtValidator implements OAuth2TokenValidator<Jwt> {
    private static final OAuth2Error INVALID_TOKEN = new OAuth2Error("invalid_token");

    private final Duration clockSkew;

    public IdentityJwtValidator(Duration clockSkew) {
        this.clockSkew = clockSkew;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (!IdentityJwtClaims.ISSUER.equals(jwt.getClaimAsString("iss"))) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
        }
        if (!isUuid(jwt.getSubject()) || !isUuid(jwt.getClaimAsString(IdentityJwtClaims.SESSION_ID))) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
        }
        Instant issuedAt = jwt.getIssuedAt();
        if (issuedAt == null || issuedAt.isAfter(Instant.now().plus(clockSkew))) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
        }
        return OAuth2TokenValidatorResult.success();
    }

    private boolean isUuid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
