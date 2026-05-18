package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileCapabilityRepository {

    ProfileCapability save(ProfileCapability capability);

    Optional<ProfileCapability> findByAccountIdAndCapability(UUID accountId, ProfileCapabilityEnum capability);

    List<ProfileCapability> findAllByAccountId(UUID accountId);
}
