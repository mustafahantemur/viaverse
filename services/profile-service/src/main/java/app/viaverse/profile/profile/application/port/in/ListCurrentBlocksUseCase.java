package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.ProfileBlock;
import java.util.List;
import java.util.UUID;

public interface ListCurrentBlocksUseCase {

    List<ProfileBlock> execute(UUID blockerAccountId);
}
