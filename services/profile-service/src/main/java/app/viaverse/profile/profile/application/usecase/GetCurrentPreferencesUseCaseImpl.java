package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.GetCurrentPreferencesUseCase;
import app.viaverse.profile.profile.application.port.out.ProfilePreferenceRepository;
import app.viaverse.web.logging.ObservedAction;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentPreferencesUseCaseImpl implements GetCurrentPreferencesUseCase {

    private final ProfilePreferenceRepository repository;

    public GetCurrentPreferencesUseCaseImpl(ProfilePreferenceRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("profile.preferences.list")
    public Map<String, String> execute(UUID accountId) {
        return repository.findAllByAccountId(accountId).stream()
                .collect(Collectors.toUnmodifiableMap(
                        preference -> preference.getKey(),
                        preference -> preference.getValueJson()
                ));
    }
}
