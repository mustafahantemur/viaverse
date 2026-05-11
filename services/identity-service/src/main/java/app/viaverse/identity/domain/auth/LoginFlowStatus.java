package app.viaverse.identity.domain.auth;

public enum LoginFlowStatus {
    OTP_REQUIRED,
    OTP_VERIFIED,
    REGISTRATION_REQUIRED,
    COMPLETED,
    EXPIRED,
    FAILED
}
