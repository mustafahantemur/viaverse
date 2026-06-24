package app.viaverse.contracts.marketplace;

import java.time.Instant;
import java.util.UUID;

public record MarketplaceRequestCreatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID requestId,
        UUID requesterAccountId,
        String category,
        String city,
        String district
) {
}
