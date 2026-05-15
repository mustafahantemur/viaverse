package app.viaverse.identity.account.application.port.out;

import app.viaverse.identity.account.domain.AccountStatus;
import java.util.UUID;

public interface AccountEventPublisher {

    void publishCreated(UUID accountId, String displayName);

    void publishStatusChanged(UUID accountId, AccountStatus newStatus);
}
