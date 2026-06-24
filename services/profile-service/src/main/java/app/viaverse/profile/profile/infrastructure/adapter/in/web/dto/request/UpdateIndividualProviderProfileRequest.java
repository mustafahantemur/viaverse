package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateIndividualProviderProfileRequest(
        @Size(max = 200) String serviceBlurb,
        @Size(max = 160) String availabilitySummary,
        boolean acceptsRemote,
        Set<MarketplaceServiceCategory> serviceCategories
) {
}
