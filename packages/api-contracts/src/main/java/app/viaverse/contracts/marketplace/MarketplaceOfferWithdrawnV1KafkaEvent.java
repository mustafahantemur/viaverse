package app.viaverse.contracts.marketplace;

import java.time.Instant;
import java.util.UUID;

public record MarketplaceOfferWithdrawnV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String schemaVersion,
        UUID offerId,
        UUID requestId,
        UUID providerAccountId
) {
}
