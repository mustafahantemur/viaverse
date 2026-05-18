package app.viaverse.profile.profile.application.port.in;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import java.util.Set;
import java.util.UUID;

public interface UpdateBusinessDraftUseCase {

    BusinessProfile execute(Command command);

    record Command(
            UUID accountId,
            String legalName,
            String tradeName,
            BusinessSectorEnum sector,
            String taxId,
            String addressLine,
            String district,
            String city,
            String country,
            String phone,
            String emailPublic,
            UUID logoMediaId,
            String openingHoursJson,
            Set<MarketplaceServiceCategory> serviceCategories
    ) {
    }
}
