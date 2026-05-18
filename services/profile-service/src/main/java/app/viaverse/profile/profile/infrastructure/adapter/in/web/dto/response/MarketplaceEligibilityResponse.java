package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import java.util.Set;
import java.util.UUID;

public record MarketplaceEligibilityResponse(
        UUID accountId,
        boolean canOffer,
        ActiveModeEnum activeMode,
        boolean individualProviderEnabled,
        boolean businessEnabled,
        BusinessVerificationStatusEnum businessVerificationStatus,
        Set<MarketplaceServiceCategory> individualProviderServiceCategories,
        Set<MarketplaceServiceCategory> businessServiceCategories,
        boolean individualProviderAcceptsRemote,
        String businessDistrict,
        String businessCity
) {
}
