package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.model.ProfilePreference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfilePreferenceRepository {

    ProfilePreference save(ProfilePreference preference);

    Optional<ProfilePreference> findByAccountIdAndKey(UUID accountId, String key);

    List<ProfilePreference> findAllByAccountId(UUID accountId);
}
