package app.viaverse.contracts.marketplace;

import java.time.Instant;
import java.util.UUID;

public record MarketplaceJobStartedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID jobId,
        UUID requesterAccountId,
        UUID providerAccountId
) {
}
