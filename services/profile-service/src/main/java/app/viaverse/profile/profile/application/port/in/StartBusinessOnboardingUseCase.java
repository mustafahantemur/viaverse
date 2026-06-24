package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import java.util.UUID;

public interface StartBusinessOnboardingUseCase {

    Result execute(UUID accountId);

    record Result(ProfileCapability capability, BusinessProfile businessProfile) {
    }
}
