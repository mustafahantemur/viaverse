package app.viaverse.marketplace.marketplace.application.port.out;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import java.util.Set;
import java.util.UUID;

public interface ProviderEligibilityGateway {

    Eligibility getEligibility(UUID accountId);

    record Eligibility(
            UUID accountId,
            boolean canOffer,
            String activeMode,
            boolean individualProviderEnabled,
            boolean businessEnabled,
            String businessVerificationStatus,
            Set<MarketplaceServiceCategory> individualProviderServiceCategories,
            Set<MarketplaceServiceCategory> businessServiceCategories,
            boolean individualProviderAcceptsRemote,
            String businessDistrict,
            String businessCity
    ) {
    }
}
