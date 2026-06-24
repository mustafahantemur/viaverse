package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import java.util.UUID;

public interface DisableIndividualProviderUseCase {

    Result execute(UUID accountId);

    record Result(ProfileCapability capability, Profile profile) {
    }
}
