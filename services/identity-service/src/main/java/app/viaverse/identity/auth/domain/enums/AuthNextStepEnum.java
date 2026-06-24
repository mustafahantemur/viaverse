package app.viaverse.identity.auth.domain.enums;

/**
 * Tells the client what the next step in the auth flow is.
 *
 * <ul>
 *   <li>{@link #PASSWORD_REQUIRED} — server recognises the identifier; client
 *       should call {@code POST /auth/password-login} with email + password.</li>
 *   <li>{@link #OTP_REQUIRED} — identifier is unknown to the server, so we
 *       started a registration flow and dispatched an OTP. Client should call
 *       {@code POST /auth/register/verify-otp} with the OTP.</li>
 *   <li>{@link #REGISTRATION_REQUIRED} — OTP verified; client must finish
 *       registration by calling {@code POST /auth/register/complete} with
 *       a password, display name, and consents.</li>
 *   <li>{@link #TOTP_REQUIRED} — primary credential succeeded but the account
 *       has 2FA enabled. Client should call {@code POST /auth/verify-totp}
 *       with the partial-auth token plus the 6-digit code.</li>
 *   <li>{@link #AUTHENTICATED} — full session issued, client has access +
 *       refresh tokens.</li>
 * </ul>
 */
public enum AuthNextStepEnum {
    PASSWORD_REQUIRED,
    OTP_REQUIRED,
    EMAIL_VERIFICATION_REQUIRED,
    PHONE_VERIFICATION_REQUIRED,
    REGISTRATION_REQUIRED,
    TOTP_REQUIRED,
    AUTHENTICATED
}
