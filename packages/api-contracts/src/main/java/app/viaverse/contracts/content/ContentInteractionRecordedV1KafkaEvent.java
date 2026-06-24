package app.viaverse.contracts.content;

import java.time.Instant;
import java.util.UUID;

public record ContentInteractionRecordedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String schemaVersion,
        UUID interactionId,
        UUID viewerAccountId,
        UUID postId,
        String signalType,
        String surface,
        Integer position,
        Long dwellTimeMs,
        UUID sessionId
) {
}
