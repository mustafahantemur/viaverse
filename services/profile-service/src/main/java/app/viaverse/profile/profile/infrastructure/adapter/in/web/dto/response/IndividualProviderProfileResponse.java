package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import java.util.Set;

public record IndividualProviderProfileResponse(
        String serviceBlurb,
        String availabilitySummary,
        boolean acceptsRemote,
        Set<MarketplaceServiceCategory> serviceCategories,
        String providerTermsVersionAccepted
) {
}
