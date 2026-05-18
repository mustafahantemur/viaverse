package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.model.Profile;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {

    Profile save(Profile profile);

    Optional<Profile> findByAccountId(UUID accountId);
}
