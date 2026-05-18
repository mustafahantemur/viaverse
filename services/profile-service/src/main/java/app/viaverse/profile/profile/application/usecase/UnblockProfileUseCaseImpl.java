package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.UnblockProfileUseCase;
import app.viaverse.profile.profile.application.port.out.ProfileBlockRepository;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.web.logging.ObservedAction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnblockProfileUseCaseImpl implements UnblockProfileUseCase {

    private final ProfileBlockRepository repository;
    private final ProfileEventPublisher eventPublisher;

    public UnblockProfileUseCaseImpl(
            ProfileBlockRepository repository,
            ProfileEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @ObservedAction("profile.blocks.delete")
    @Transactional
    public boolean execute(Command command) {
        return repository.findByBlockerAccountIdAndBlockedAccountId(
                        command.blockerAccountId(),
                        command.blockedAccountId()
                )
                .map(this::delete)
                .orElse(false);
    }

    private boolean delete(ProfileBlock block) {
        repository.delete(block);
        eventPublisher.publishUnblocked(block);
        return true;
    }
}
