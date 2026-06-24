package app.viaverse.contracts.profile.profile;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted after profile-service provisions the base profile aggregate.
 */
public record ProfileCreatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        String displayName,
        String publicVisibility
) {
}
