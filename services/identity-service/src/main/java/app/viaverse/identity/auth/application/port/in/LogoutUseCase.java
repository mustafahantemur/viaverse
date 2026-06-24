package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.shared.aspect.AuditableCommand;
import java.util.UUID;

public interface LogoutUseCase {

    void execute(Command command);

    record Command(UUID principalAccountId, UUID principalSessionId, String refreshToken)
            implements AuditableCommand {
        @Override
        public UUID accountId() {
            return principalAccountId;
        }
    }
}
