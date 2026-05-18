package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetCurrentProfileUseCase {

    Result execute(UUID accountId);

    record Result(
            Profile profile,
            List<ProfileCapability> capabilities,
            Optional<IndividualProviderProfile> individualProviderProfile,
            Optional<BusinessProfile> businessProfile
    ) {
    }
}
