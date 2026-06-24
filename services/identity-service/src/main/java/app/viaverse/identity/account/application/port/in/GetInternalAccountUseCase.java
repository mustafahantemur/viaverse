package app.viaverse.identity.account.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface GetInternalAccountUseCase {

    Result execute(UUID accountId);

    record Result(
            UUID accountId,
            String displayName,
            String firstName,
            String lastName,
            Instant createdAt
    ) {
    }
}
