package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.PutCurrentPreferenceUseCase;
import app.viaverse.profile.profile.application.port.out.ProfilePreferenceRepository;
import app.viaverse.profile.profile.domain.model.ProfilePreference;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PutCurrentPreferenceUseCaseImpl implements PutCurrentPreferenceUseCase {

    private final ProfilePreferenceRepository repository;
    private final Clock clock;

    public PutCurrentPreferenceUseCaseImpl(ProfilePreferenceRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.preferences.put")
    @Transactional
    public ProfilePreference execute(Command command) {
        Instant now = clock.instant();
        ProfilePreference next = repository.findByAccountIdAndKey(command.accountId(), command.key())
                .map(current -> new ProfilePreference(
                        current.getAccountId(),
                        current.getKey(),
                        command.valueJson(),
                        current.getCreatedAt(),
                        now,
                        current.getVersion()
                ))
                .orElseGet(() -> new ProfilePreference(
                        command.accountId(),
                        command.key(),
                        command.valueJson(),
                        now,
                        now,
                        0
                ));
        return repository.save(next);
    }
}
