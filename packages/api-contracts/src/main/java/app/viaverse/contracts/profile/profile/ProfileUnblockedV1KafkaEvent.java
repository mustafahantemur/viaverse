package app.viaverse.contracts.profile.profile;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted when one account removes a block relationship.
 */
public record ProfileUnblockedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID blockerAccountId,
        UUID blockedAccountId
) {
}
