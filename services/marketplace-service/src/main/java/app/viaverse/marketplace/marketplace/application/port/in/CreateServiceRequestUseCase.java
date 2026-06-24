package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import java.util.List;
import java.util.UUID;

public interface CreateServiceRequestUseCase {

    ServiceRequest execute(Command command);

    record Command(
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
            List<UUID> mediaAssetIds
    ) {
    }
}
