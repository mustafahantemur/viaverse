package app.viaverse.profile.profile.application.port.in;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import java.util.Set;
import java.util.UUID;

public interface UpdateCurrentIndividualProviderProfileUseCase {

    IndividualProviderProfile execute(Command command);

    record Command(
            UUID accountId,
            String serviceBlurb,
            String availabilitySummary,
            boolean acceptsRemote,
            Set<MarketplaceServiceCategory> serviceCategories
    ) {
    }
}
