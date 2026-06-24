package app.viaverse.profile.profile.application.port.in;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import java.util.Set;
import java.util.UUID;

public interface GetMarketplaceEligibilityUseCase {

    Result execute(UUID accountId);

    record Result(
            UUID accountId,
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
        public boolean canOffer() {
            return individualProviderEnabled || businessEnabled;
        }
    }
}
