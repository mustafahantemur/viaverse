package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response;

import app.viaverse.marketplace.marketplace.domain.enums.JobStatusEnum;
import java.time.Instant;
import java.util.UUID;

public record JobResponse(
        UUID id,
        UUID requestId,
        UUID acceptedOfferId,
        UUID requesterAccountId,
        UUID providerAccountId,
        long agreedAmountMinor,
        String currency,
        JobStatusEnum status,
        Instant createdAt,
        Instant updatedAt
) {
}
