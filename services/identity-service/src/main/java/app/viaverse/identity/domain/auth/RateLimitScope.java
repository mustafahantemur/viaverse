package app.viaverse.identity.domain.auth;

public enum RateLimitScope {
    AUTH_START_IDENTIFIER,
    AUTH_START_IP,
    AUTH_START_DEVICE,
    AUTH_START_RESEND,
    OTP_VERIFY_FLOW,
    OTP_VERIFY_IDENTIFIER,
    OTP_VERIFY_IP
}
