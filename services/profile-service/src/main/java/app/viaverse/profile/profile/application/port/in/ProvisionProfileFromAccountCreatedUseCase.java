package app.viaverse.profile.profile.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface ProvisionProfileFromAccountCreatedUseCase {

    Result provision(Command command);

    record Command(
            UUID eventId,
            Instant occurredAt,
            UUID accountId,
            String displayName,
            String firstName,
            String lastName
    ) {
    }

    record Result(UUID accountId, boolean created) {
    }
}
