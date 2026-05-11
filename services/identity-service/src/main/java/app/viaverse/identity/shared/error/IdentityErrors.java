package app.viaverse.identity.shared.error;

import app.viaverse.shared.kernel.error.AppErrorCode;
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
