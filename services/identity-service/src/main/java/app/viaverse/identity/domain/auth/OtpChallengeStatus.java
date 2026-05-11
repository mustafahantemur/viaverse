package app.viaverse.identity.domain.auth;

public enum OtpChallengeStatus {
    ACTIVE,
    VERIFIED,
    EXPIRED,
    LOCKED
}
