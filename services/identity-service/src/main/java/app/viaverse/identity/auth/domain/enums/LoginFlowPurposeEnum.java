package app.viaverse.identity.auth.domain.enums;

/**
 * What an {@code auth_login_flow} row is actually proving. Each purpose has
 * its own downstream effect on success and its own rate-limit profile.
 *
 * <ul>
 *   <li>{@link #REGISTRATION} — new account; OTP proves the identifier
 *       belongs to the user before we accept a password. On success we issue
 *       a registration token that {@code /auth/register/complete} consumes.</li>
 *   <li>{@link #IDENTIFIER_VERIFY} — authenticated user adding a new
 *       email/phone to their account. OTP proves ownership of the new
 *       identifier; on success we attach it to the account.</li>
 *   <li>{@link #TWO_FACTOR_SETUP} — proof-of-ownership of the primary
 *       identifier when enrolling or disabling TOTP-based 2FA.</li>
 *   <li>{@link #PASSWORD_RESET} — recover account access via OTP to a
 *       known verified identifier; on success the user picks a new
 *       password.</li>
 * </ul>
 */
public enum LoginFlowPurposeEnum {
    REGISTRATION,
    IDENTIFIER_VERIFY,
    TWO_FACTOR_SETUP,
    PASSWORD_RESET
}
