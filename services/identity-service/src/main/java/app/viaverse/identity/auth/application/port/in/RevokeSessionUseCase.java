package app.viaverse.identity.auth.application.port.in;

import java.util.UUID;

public interface RevokeSessionUseCase {

    void execute(Command command);

    record Command(UUID accountId, UUID sessionId, UUID currentSessionId, boolean revokeAllExceptCurrent) {}
}
