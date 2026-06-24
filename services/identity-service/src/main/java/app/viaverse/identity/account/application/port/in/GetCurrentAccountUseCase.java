package app.viaverse.identity.account.application.port.in;

import app.viaverse.identity.account.domain.model.Account;
import java.util.UUID;

public interface GetCurrentAccountUseCase {

    Account execute(Command command);

    record Command(UUID accountId, UUID sessionId) {}
}
