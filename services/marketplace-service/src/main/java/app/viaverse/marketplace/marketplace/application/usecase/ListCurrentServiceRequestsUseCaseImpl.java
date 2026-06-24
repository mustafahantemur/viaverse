package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.ListCurrentServiceRequestsUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListCurrentServiceRequestsUseCaseImpl implements ListCurrentServiceRequestsUseCase {

    private final ServiceRequestRepository repository;

    public ListCurrentServiceRequestsUseCaseImpl(ServiceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("marketplace.request.list_current")
    public List<ServiceRequest> execute(UUID requesterAccountId) {
        return repository.findAllByRequesterAccountId(requesterAccountId);
    }
}
