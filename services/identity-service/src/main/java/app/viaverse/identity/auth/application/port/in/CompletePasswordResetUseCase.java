package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.shared.aspect.AuditableResult;
import java.util.UUID;

/**
 * Final step of forgot-password: consumes the reset token, validates the new
 * password against {@code PasswordPolicy}, rotates the account's password.
 * Does not auto-issue a session — the user goes through password-login on
 * the next screen so the new credential is exercised end-to-end.
 */
public interface CompletePasswordResetUseCase {

    Result execute(Command command);

    record Command(String resetToken, String newPassword) {}

    record Result(UUID accountId) implements AuditableResult {}
}
