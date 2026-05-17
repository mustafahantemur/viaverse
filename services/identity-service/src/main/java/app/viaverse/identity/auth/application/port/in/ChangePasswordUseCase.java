package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.shared.aspect.AuditableCommand;
import java.util.UUID;

/**
 * Authenticated password change. The user must prove they know their current
 * password — a stolen session alone cannot rotate the credential. Social-only
 * accounts (no password set yet) can use this to set their first password by
 * passing {@code currentPassword=null}.
 */
public interface ChangePasswordUseCase {

    void execute(Command command);

    record Command(
            UUID accountId,
            String currentPassword,
            String newPassword
    ) implements AuditableCommand {}
}
