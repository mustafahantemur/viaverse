package app.viaverse.identity.auth.domain.enums;

public enum RateLimitScopeEnum {
    AUTH_START_IDENTIFIER,
    AUTH_START_IP,
    AUTH_START_DEVICE,
    AUTH_START_RESEND,
    OTP_VERIFY_FLOW,
    OTP_VERIFY_IDENTIFIER,
    OTP_VERIFY_IP
}
