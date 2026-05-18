package app.viaverse.contracts.marketplace;

import java.time.Instant;
import java.util.UUID;

public record MarketplaceRequestCancelledV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String schemaVersion,
        UUID requestId,
        UUID requesterAccountId
) {
}
