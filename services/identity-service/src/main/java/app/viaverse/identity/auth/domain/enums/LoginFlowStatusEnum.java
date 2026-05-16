package app.viaverse.identity.auth.domain.enums;

public enum LoginFlowStatusEnum {
    OTP_REQUIRED,
    OTP_VERIFIED,
    REGISTRATION_REQUIRED,
    COMPLETED,
    EXPIRED,
    FAILED
}
