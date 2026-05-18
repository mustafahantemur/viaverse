package app.viaverse.contracts.trust.score;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted when trust-gamification-service recalculates the account trust snapshot.
 */
public record TrustScoreUpdatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        int score,
        String level,
        String badge
) {
}
