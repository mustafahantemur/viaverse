package app.viaverse.identity.auth.application.port.in;

import java.util.UUID;

public interface LogoutUseCase {

    void execute(Command command);

    record Command(UUID principalAccountId, UUID principalSessionId, String refreshToken) {}
}
