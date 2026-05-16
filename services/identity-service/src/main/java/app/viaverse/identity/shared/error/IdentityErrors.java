package app.viaverse.identity.shared.error;

import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.TechnicalException;
import app.viaverse.shared.kernel.error.ValidationException;
import java.util.Map;
import org.springframework.http.HttpStatus;

public final class IdentityErrors {
    private IdentityErrors() {
    }

    public static IdentityException invalidAccessToken() {
        return unauthorized(AppErrorCode.AUTH_INVALID_ACCESS_TOKEN, "Invalid access token");
    }

    public static IdentityException accessTokenExpired() {
        return unauthorized(AppErrorCode.AUTH_ACCESS_TOKEN_EXPIRED, "Access token expired");
    }

    public static IdentityException bearerTokenRequired() {
        return unauthorized(AppErrorCode.AUTH_BEARER_TOKEN_REQUIRED, "Bearer token is required");
    }

    public static IdentityException invalidAuthFlow(Map<String, String> fieldErrors) {
        return badRequest(AppErrorCode.AUTH_INVALID_AUTH_FLOW, "Invalid auth flow", fieldErrors);
    }

    public static IdentityException authFlowNotWaitingForOtp() {
        return badRequest(
                AppErrorCode.AUTH_INVALID_AUTH_FLOW,
                "Auth flow is not waiting for OTP",
                Map.of("flowId", "is not active")
        );
    }

    public static IdentityException invalidOtp() {
        return unauthorized(AppErrorCode.AUTH_INVALID_OTP, "Invalid OTP");
    }

    public static IdentityException otpExpired() {
        return unauthorized(AppErrorCode.AUTH_OTP_EXPIRED, "OTP expired");
    }

    public static IdentityException invalidRegistrationToken() {
        return unauthorized(AppErrorCode.AUTH_INVALID_REGISTRATION_TOKEN, "Invalid registration token");
    }

    public static IdentityException registrationTokenExpired() {
        return unauthorized(AppErrorCode.AUTH_REGISTRATION_TOKEN_EXPIRED, "Registration token expired");
    }

    public static IdentityException requiredConsentsMissing(Map<String, String> fieldErrors) {
        return badRequest(
                AppErrorCode.AUTH_REQUIRED_CONSENTS_MISSING,
                "Required consents are missing",
                fieldErrors
        );
    }

    public static IdentityException displayNameRequired() {
        return badRequest(
                AppErrorCode.AUTH_DISPLAY_NAME_REQUIRED,
                "Display name is required",
                Map.of("displayName", "must not be blank")
        );
    }

    public static IdentityException refreshTokenRequired() {
        return badRequest(
                AppErrorCode.AUTH_INVALID_REFRESH_TOKEN,
                "Refresh token is required",
                Map.of("refreshToken", "must not be blank")
        );
    }

    public static IdentityException invalidRefreshToken() {
        return unauthorized(AppErrorCode.AUTH_INVALID_REFRESH_TOKEN, "Invalid refresh token");
    }

    public static IdentityException refreshTokenExpired() {
        return unauthorized(AppErrorCode.AUTH_REFRESH_TOKEN_EXPIRED, "Refresh token expired");
    }

    public static IdentityException invalidSession() {
        return unauthorized(AppErrorCode.AUTH_SESSION_EXPIRED, "Invalid session");
    }

    public static IdentityException inactiveSession() {
        return unauthorized(AppErrorCode.AUTH_SESSION_EXPIRED, "Session is not active");
    }

    public static IdentityException sessionExpired() {
        return unauthorized(AppErrorCode.AUTH_SESSION_EXPIRED, "Session expired");
    }

    public static IdentityException accountNotActive() {
        return unauthorized(AppErrorCode.AUTH_ACCOUNT_NOT_ACTIVE, "Account is not active");
    }

    public static IdentityException invalidSocialToken() {
        return unauthorized(AppErrorCode.AUTH_INVALID_SOCIAL_TOKEN, "Invalid social identity token");
    }

    public static IdentityException invalidAdminInvitationToken() {
        return unauthorized(AppErrorCode.AUTH_INVALID_ADMIN_INVITATION_TOKEN, "Invalid admin invitation token");
    }

    public static IdentityException adminInvitationTokenExpired() {
        return unauthorized(AppErrorCode.AUTH_ADMIN_INVITATION_TOKEN_EXPIRED, "Admin invitation token expired");
    }

    public static IdentityException providerDisabled(String provider) {
        return badRequest(
                AppErrorCode.AUTH_PROVIDER_DISABLED,
                "Authentication provider is disabled",
                Map.of("provider", provider)
        );
    }

    public static ValidationException identifierRequired() {
        return new ValidationException("Identifier is required", Map.of("identifier", "must not be blank"));
    }

    public static ValidationException invalidEmailIdentifier() {
        return new ValidationException("Invalid identifier", Map.of("identifier", "must be a valid email"));
    }

    public static ValidationException invalidIdentifier() {
        return new ValidationException("Invalid identifier", Map.of("identifier", "must be a valid email or phone"));
    }

    public static TechnicalException jwtSecretRequired() {
        return technicalConfiguration("Identity JWT secret must be configured");
    }

    public static TechnicalException jwtSecretTooWeak() {
        return technicalConfiguration("Identity JWT secret must be at least 32 bytes");
    }

    public static TechnicalException debugOtpProfileInvalid() {
        return technicalConfiguration("Debug OTP can only be enabled in local or test profiles");
    }

    public static TechnicalException debugSeedUsersProfileInvalid() {
        return technicalConfiguration("Debug seed users can only be enabled in local or test profiles");
    }

    public static TechnicalException debugOtpFixedValueRequired() {
        return technicalConfiguration("Debug OTP is enabled but no fixed OTP is configured");
    }

    public static TechnicalException smsProviderDisabled() {
        return new TechnicalException(
                AppErrorCode.AUTH_PROVIDER_DISABLED,
                "SMS OTP provider is disabled"
        );
    }

    public static TechnicalException otpDeliveryProviderMissing(String identifierType) {
        return new TechnicalException(
                AppErrorCode.AUTH_PROVIDER_DISABLED,
                "No OTP delivery provider configured for identifier type " + identifierType
        );
    }

    public static TechnicalException smtpConfigurationInvalid() {
        return technicalConfiguration("SMTP OTP configuration is incomplete");
    }

    public static TechnicalException smtpDeliveryFailed(Throwable cause) {
        return new TechnicalException(
                AppErrorCode.TECHNICAL_EMAIL_DELIVERY_FAILED,
                "Unable to deliver email OTP",
                cause
        );
    }

    public static TechnicalException netgsmConfigurationInvalid() {
        return technicalConfiguration("NetGSM SMS configuration is incomplete");
    }

    public static TechnicalException socialProviderConfigurationInvalid(String provider) {
        return technicalConfiguration(provider + " social auth configuration is incomplete");
    }

    public static TechnicalException tokenHashFailed(Throwable cause) {
        return new TechnicalException(
                AppErrorCode.TECHNICAL_TOKEN_HASH_FAILED,
                "Unable to hash token",
                cause
        );
    }

    public static TechnicalException jwtEncodingFailed(Throwable cause) {
        return new TechnicalException(
                AppErrorCode.TECHNICAL_JWT_ENCODING_FAILED,
                "Unable to encode access token",
                cause
        );
    }

    public static TechnicalException smsDeliveryFailed(Throwable cause) {
        return new TechnicalException(
                AppErrorCode.TECHNICAL_SMS_DELIVERY_FAILED,
                "Unable to deliver SMS OTP",
                cause
        );
    }

    public static TechnicalException outboxSerializationFailed(Throwable cause) {
        return new TechnicalException(
                AppErrorCode.TECHNICAL_ERROR,
                "Failed to serialize outbox event payload",
                cause
        );
    }

    public static TechnicalException rateLimitBackendUnavailable(Throwable cause) {
        return new TechnicalException(
                AppErrorCode.TECHNICAL_ERROR,
                "Rate-limit backend is unavailable; failing closed",
                cause
        );
    }

    public static TechnicalException smsDeliveryRejected() {
        return new TechnicalException(
                AppErrorCode.TECHNICAL_SMS_DELIVERY_FAILED,
                "SMS provider rejected OTP delivery"
        );
    }

    private static TechnicalException technicalConfiguration(String message) {
        return new TechnicalException(AppErrorCode.AUTH_PROVIDER_CONFIGURATION_INVALID, message);
    }

    private static IdentityException unauthorized(AppErrorCode code, String message) {
        return new IdentityException(code, message, HttpStatus.UNAUTHORIZED);
    }

    private static IdentityException badRequest(
            AppErrorCode code,
            String message,
            Map<String, String> fieldErrors
    ) {
        return new IdentityException(code, message, HttpStatus.BAD_REQUEST, fieldErrors);
    }
}
