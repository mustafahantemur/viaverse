package app.viaverse.trustgamification.trust.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface BootstrapTrustFromProfileCreatedUseCase {

    Result bootstrap(Command command);

    record Command(UUID eventId, Instant occurredAt, UUID accountId) {
    }

    record Result(UUID accountId, boolean created) {
    }
}
