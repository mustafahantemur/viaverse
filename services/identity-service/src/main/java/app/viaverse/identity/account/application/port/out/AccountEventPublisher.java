package app.viaverse.identity.account.application.port.out;

import app.viaverse.identity.account.domain.AccountStatusEnum;
import java.util.UUID;

public interface AccountEventPublisher {

    void publishCreated(UUID accountId, String displayName, String firstName, String lastName);

    void publishStatusChanged(UUID accountId, AccountStatusEnum newStatus);
}
