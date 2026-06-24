package app.viaverse.identity.account.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface MirrorProfileUpdatedUseCase {

    Result mirror(Command command);

    record Command(
            UUID eventId,
            Instant occurredAt,
            UUID accountId,
            String displayName,
            String firstName,
            String lastName
    ) {
    }

    record Result(UUID accountId, boolean updated) {
    }
}
