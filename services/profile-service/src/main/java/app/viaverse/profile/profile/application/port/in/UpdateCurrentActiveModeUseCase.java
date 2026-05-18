package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.model.Profile;
import java.util.UUID;

public interface UpdateCurrentActiveModeUseCase {

    Profile execute(Command command);

    record Command(UUID accountId, ActiveModeEnum activeMode) {
    }
}
