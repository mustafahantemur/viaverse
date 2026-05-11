package app.viaverse.identity.shared.audit;

public enum IdentityAuditEvent {
    ACCOUNT_CREATED,
    LOGIN_SUCCEEDED,
    LOGIN_FAILED,
    OTP_VERIFIED,
    OTP_FAILED,
    REFRESH_TOKEN_ROTATED,
    REFRESH_TOKEN_REUSED,
    SESSION_REVOKED,
    CONSENT_ACCEPTED
}
