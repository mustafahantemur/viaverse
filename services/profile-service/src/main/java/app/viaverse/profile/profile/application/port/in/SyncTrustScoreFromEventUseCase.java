package app.viaverse.profile.profile.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface SyncTrustScoreFromEventUseCase {

    Result sync(Command command);

    record Command(
            UUID eventId,
            Instant occurredAt,
            UUID accountId,
            int score,
            String level,
            String badge
    ) {
    }

    record Result(UUID accountId, boolean updated) {
    }
}
