package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response;

import app.viaverse.marketplace.marketplace.domain.enums.OfferStatusEnum;
import java.time.Instant;
import java.util.UUID;

public record OfferResponse(
        UUID id,
        UUID requestId,
        UUID providerAccountId,
        long amountMinor,
        String currency,
        String message,
        OfferStatusEnum status,
        Instant createdAt,
        Instant updatedAt
) {
}
