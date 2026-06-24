package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.enums.PublicVisibilityEnum;
import app.viaverse.profile.profile.domain.model.Profile;
import java.util.UUID;

public interface UpdateCurrentProfileUseCase {

    Profile execute(Command command);

    record Command(
            UUID accountId,
            String displayName,
            String firstName,
            String lastName,
            UUID avatarMediaId,
            String headline,
            String bio,
            String locale,
            String timezone,
            PublicVisibilityEnum publicVisibility
    ) {
    }
}
