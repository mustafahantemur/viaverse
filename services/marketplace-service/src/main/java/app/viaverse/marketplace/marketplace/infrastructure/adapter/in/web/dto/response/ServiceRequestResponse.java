package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.domain.enums.ServiceRequestStatusEnum;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ServiceRequestResponse(
        UUID id,
        UUID requesterAccountId,
        String title,
        String description,
        MarketplaceServiceCategory category,
        Long budgetMinAmountMinor,
        Long budgetMaxAmountMinor,
        String currency,
        boolean remoteAllowed,
        String district,
        String city,
        List<UUID> mediaAssetIds,
        ServiceRequestStatusEnum status,
        Instant createdAt,
        Instant updatedAt
) {
}
