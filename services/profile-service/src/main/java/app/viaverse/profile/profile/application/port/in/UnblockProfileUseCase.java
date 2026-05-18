package app.viaverse.profile.profile.application.port.in;

import java.util.UUID;

public interface UnblockProfileUseCase {

    boolean execute(Command command);

    record Command(UUID blockerAccountId, UUID blockedAccountId) {
    }
}
