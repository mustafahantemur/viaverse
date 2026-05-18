package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.BlockProfileUseCase;
import app.viaverse.profile.profile.application.port.out.ProfileBlockRepository;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.shared.kernel.error.ValidationException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlockProfileUseCaseImpl implements BlockProfileUseCase {

    private final ProfileBlockRepository repository;
    private final ProfileEventPublisher eventPublisher;
    private final Clock clock;

    public BlockProfileUseCaseImpl(
            ProfileBlockRepository repository,
            ProfileEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.blocks.create")
    @Transactional
    public ProfileBlock execute(Command command) {
        if (command.blockerAccountId().equals(command.blockedAccountId())) {
            throw new ValidationException("Cannot block own profile");
        }
        return repository.findByBlockerAccountIdAndBlockedAccountId(
                        command.blockerAccountId(),
                        command.blockedAccountId()
                )
                .orElseGet(() -> create(command));
    }

    private ProfileBlock create(Command command) {
        Instant now = clock.instant();
        ProfileBlock saved = repository.save(new ProfileBlock(
                command.blockerAccountId(),
                command.blockedAccountId(),
                command.reason(),
                now,
                now,
                0
        ));
        eventPublisher.publishBlocked(saved);
        return saved;
    }
}
