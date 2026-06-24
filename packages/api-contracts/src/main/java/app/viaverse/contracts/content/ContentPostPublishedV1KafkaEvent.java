package app.viaverse.contracts.content;

import java.time.Instant;
import java.util.UUID;

public record ContentPostPublishedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String schemaVersion,
        UUID postId,
        UUID authorAccountId,
        String postType,
        String city,
        String district,
        Instant publishedAt
) {
}
