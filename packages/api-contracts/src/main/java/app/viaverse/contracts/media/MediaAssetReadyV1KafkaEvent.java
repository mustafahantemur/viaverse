package app.viaverse.contracts.media;

import java.time.Instant;
import java.util.UUID;

public record MediaAssetReadyV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String schemaVersion,
        UUID assetId,
        UUID ownerAccountId,
        String assetKind,
        String contentType,
        long byteSize
) {
}
