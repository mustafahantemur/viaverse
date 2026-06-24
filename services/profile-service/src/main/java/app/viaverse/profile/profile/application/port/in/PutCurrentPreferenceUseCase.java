package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.model.ProfilePreference;
import java.util.UUID;

public interface PutCurrentPreferenceUseCase {

    ProfilePreference execute(Command command);

    record Command(UUID accountId, String key, String valueJson) {
    }
}
