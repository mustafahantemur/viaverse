package app.viaverse.contracts.marketplace;

import java.time.Instant;
import java.util.UUID;

public record MarketplaceOfferSubmittedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID offerId,
        UUID requestId,
        UUID providerAccountId,
        long amountMinor,
        String currency
) {
}
