package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.UpdateCurrentIndividualProviderProfileUseCase;
import app.viaverse.profile.profile.application.port.out.IndividualProviderProfileRepository;
import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCurrentIndividualProviderProfileUseCaseImpl
        implements UpdateCurrentIndividualProviderProfileUseCase {

    private final IndividualProviderProfileRepository repository;
    private final Clock clock;

    public UpdateCurrentIndividualProviderProfileUseCaseImpl(
            IndividualProviderProfileRepository repository,
            Clock clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.individual-provider.update")
    @Transactional
    public IndividualProviderProfile execute(Command command) {
        IndividualProviderProfile current = repository.findByAccountId(command.accountId())
                .orElseThrow(() -> new NotFoundException("Individual provider profile not found"));
        return repository.save(current.updateSelfView(
                command.serviceBlurb(),
                command.availabilitySummary(),
                command.acceptsRemote(),
                command.serviceCategories(),
                clock.instant()
        ));
    }
}
