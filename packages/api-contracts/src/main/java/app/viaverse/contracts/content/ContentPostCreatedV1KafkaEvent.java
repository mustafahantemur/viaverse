package app.viaverse.contracts.content;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ContentPostCreatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String schemaVersion,
        UUID postId,
        UUID authorAccountId,
        String postType,
        String city,
        String district,
        List<UUID> mediaAssetIds
) {
}
