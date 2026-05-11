package app.viaverse.identity.auth.domain.enums;

public enum LoginFlowStatus {
    OTP_REQUIRED,
    OTP_VERIFIED,
    REGISTRATION_REQUIRED,
    COMPLETED,
    EXPIRED,
    FAILED
}
