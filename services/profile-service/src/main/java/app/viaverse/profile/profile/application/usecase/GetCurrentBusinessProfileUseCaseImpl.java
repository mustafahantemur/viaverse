package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.GetCurrentBusinessProfileUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentBusinessProfileUseCaseImpl implements GetCurrentBusinessProfileUseCase {

    private final BusinessProfileRepository repository;

    public GetCurrentBusinessProfileUseCaseImpl(BusinessProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("profile.business.current")
    public BusinessProfile execute(UUID accountId) {
        return repository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Business profile not found"));
    }
}
