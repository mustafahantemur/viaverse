package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.GetCurrentIndividualProviderProfileUseCase;
import app.viaverse.profile.profile.application.port.out.IndividualProviderProfileRepository;
import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentIndividualProviderProfileUseCaseImpl
        implements GetCurrentIndividualProviderProfileUseCase {

    private final IndividualProviderProfileRepository repository;

    public GetCurrentIndividualProviderProfileUseCaseImpl(IndividualProviderProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("profile.individual-provider.current")
    public IndividualProviderProfile execute(UUID accountId) {
        return repository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Individual provider profile not found"));
    }
}
