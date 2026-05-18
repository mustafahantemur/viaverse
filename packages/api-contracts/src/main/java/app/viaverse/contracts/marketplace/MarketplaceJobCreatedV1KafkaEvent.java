package app.viaverse.contracts.marketplace;

import java.time.Instant;
import java.util.UUID;

public record MarketplaceJobCreatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID jobId,
        UUID requestId,
        UUID acceptedOfferId,
        UUID requesterAccountId,
        UUID providerAccountId,
        long agreedAmountMinor,
        String currency
) {
}
