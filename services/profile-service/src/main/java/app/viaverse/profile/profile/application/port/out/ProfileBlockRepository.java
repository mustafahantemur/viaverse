package app.viaverse.profile.profile.application.port.out;

import app.viaverse.profile.profile.domain.model.ProfileBlock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileBlockRepository {

    ProfileBlock save(ProfileBlock block);

    Optional<ProfileBlock> findByBlockerAccountIdAndBlockedAccountId(UUID blockerAccountId, UUID blockedAccountId);

    List<ProfileBlock> findAllByBlockerAccountId(UUID blockerAccountId);

    void delete(ProfileBlock block);
}
