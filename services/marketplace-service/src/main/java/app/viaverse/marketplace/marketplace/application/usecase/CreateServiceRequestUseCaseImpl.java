package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.CreateServiceRequestUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateServiceRequestUseCaseImpl implements CreateServiceRequestUseCase {

    private final ServiceRequestRepository repository;
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public CreateServiceRequestUseCaseImpl(
            ServiceRequestRepository repository,
            MarketplaceEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("marketplace.request.create")
    @Transactional
    public ServiceRequest execute(Command command) {
        ServiceRequest saved = repository.save(ServiceRequest.create(
                command.requesterAccountId(),
                command.title(),
                command.description(),
                command.category(),
                command.budgetMinAmountMinor(),
                command.budgetMaxAmountMinor(),
                command.currency(),
                command.remoteAllowed(),
                command.district(),
                command.city(),
                command.mediaAssetIds(),
                clock.instant()
        ));
        eventPublisher.publishRequestCreated(saved);
        return saved;
    }
}
