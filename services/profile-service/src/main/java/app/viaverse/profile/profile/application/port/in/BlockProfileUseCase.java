package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.ProfileBlock;
import java.util.UUID;

public interface BlockProfileUseCase {

    ProfileBlock execute(Command command);

    record Command(UUID blockerAccountId, UUID blockedAccountId, String reason) {
    }
}
