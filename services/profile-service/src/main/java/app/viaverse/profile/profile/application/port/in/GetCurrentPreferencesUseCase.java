package app.viaverse.profile.profile.application.port.in;

import java.util.Map;
import java.util.UUID;

public interface GetCurrentPreferencesUseCase {

    Map<String, String> execute(UUID accountId);
}
