package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.UpdateCurrentProfileUseCase;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.policy.ProfilePolicy;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCurrentProfileUseCaseImpl implements UpdateCurrentProfileUseCase {

    private final ProfileRepository profileRepository;
    private final ProfileEventPublisher profileEventPublisher;
    private final Clock clock;

    public UpdateCurrentProfileUseCaseImpl(
            ProfileRepository profileRepository,
            ProfileEventPublisher profileEventPublisher,
            Clock clock
    ) {
        this.profileRepository = profileRepository;
        this.profileEventPublisher = profileEventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.update_self")
    @Transactional
    public Profile execute(Command command) {
        Profile current = profileRepository.findByAccountId(command.accountId())
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        Instant now = clock.instant();
        Profile updated = current.updateSelfView(
                coalesce(command.displayName(), current.getDisplayName()),
                coalesce(command.firstName(), current.getFirstName()),
                coalesce(command.lastName(), current.getLastName()),
                coalesce(command.avatarMediaId(), current.getAvatarMediaId()),
                coalesce(command.headline(), current.getHeadline()),
                coalesce(command.bio(), current.getBio()),
                coalesce(command.locale(), current.getLocale()),
                coalesce(command.timezone(), current.getTimezone()),
                coalesce(command.publicVisibility(), current.getPublicVisibility()),
                now
        );
        Profile scored = updated.withCompletenessScore(ProfilePolicy.computeCompleteness(updated, false, false));
        Profile saved = profileRepository.save(scored);
        profileEventPublisher.publishUpdated(saved);
        return saved;
    }

    private <T> T coalesce(T next, T current) {
        return next == null ? current : next;
    }
}
