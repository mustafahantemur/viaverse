package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.profile.profile.domain.model.BusinessProfile;

public interface ProfileEventPublisher {

    void publishCreated(Profile profile);

    void publishUpdated(Profile profile);

    void publishCapabilityEnabled(ProfileCapability capability);

    void publishCapabilityDisabled(ProfileCapability capability);

    void publishBusinessSubmitted(BusinessProfile businessProfile);

    void publishBusinessApproved(BusinessProfile businessProfile);

    void publishBusinessRejected(BusinessProfile businessProfile);

    void publishBlocked(ProfileBlock block);

    void publishUnblocked(ProfileBlock block);
}
