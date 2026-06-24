package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.model.ProfileTrustSnapshot;
import java.util.Optional;
import java.util.UUID;

public interface ProfileTrustSnapshotRepository {

    ProfileTrustSnapshot save(ProfileTrustSnapshot snapshot);

    Optional<ProfileTrustSnapshot> findByAccountId(UUID accountId);
}
