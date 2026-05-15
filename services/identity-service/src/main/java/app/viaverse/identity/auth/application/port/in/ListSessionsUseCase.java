package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.model.AuthSession;
import java.util.List;
import java.util.UUID;

public interface ListSessionsUseCase {

    List<AuthSession> execute(Command command);

    record Command(UUID accountId) {}
}
