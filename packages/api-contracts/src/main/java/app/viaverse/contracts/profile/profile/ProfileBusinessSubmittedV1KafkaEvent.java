package app.viaverse.contracts.profile.profile;

import java.time.Instant;
import java.util.UUID;

public record ProfileBusinessSubmittedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        String tradeName,
        String sector
) {
}
