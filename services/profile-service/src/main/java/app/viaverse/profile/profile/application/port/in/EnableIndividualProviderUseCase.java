package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import java.util.UUID;

public interface EnableIndividualProviderUseCase {

    Result execute(Command command);

    record Command(
            UUID accountId,
            String acceptedProviderTermsVersion,
            String serviceBlurb
    ) {
    }

    record Result(ProfileCapability capability, IndividualProviderProfile providerProfile) {
    }
}
