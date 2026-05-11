package app.viaverse.identity.domain.auth;

public enum RefreshTokenStatus {
    ACTIVE,
    REVOKED,
    ROTATED,
    EXPIRED
}
