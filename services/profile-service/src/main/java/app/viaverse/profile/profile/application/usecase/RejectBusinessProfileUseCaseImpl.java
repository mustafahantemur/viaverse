package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.RejectBusinessProfileUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RejectBusinessProfileUseCaseImpl implements RejectBusinessProfileUseCase {

    private final BusinessProfileRepository repository;
    private final ProfileEventPublisher eventPublisher;
    private final Clock clock;

    public RejectBusinessProfileUseCaseImpl(
            BusinessProfileRepository repository,
            ProfileEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.business.reject")
    @Transactional
    public BusinessProfile execute(Command command) {
        BusinessProfile rejected = repository.save(repository.findByAccountId(command.accountId())
                .orElseThrow(() -> new NotFoundException("Business profile not found"))
                .reject(command.reason(), clock.instant()));
        eventPublisher.publishBusinessRejected(rejected);
        return rejected;
    }
}
