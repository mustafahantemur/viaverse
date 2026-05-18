package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.ListOpenServiceRequestsUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.enums.ServiceRequestStatusEnum;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListOpenServiceRequestsUseCaseImpl implements ListOpenServiceRequestsUseCase {

    private final ServiceRequestRepository repository;

    public ListOpenServiceRequestsUseCaseImpl(ServiceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("marketplace.request.list_open")
    public List<ServiceRequest> execute() {
        return repository.findAllByStatus(ServiceRequestStatusEnum.OPEN);
    }
}
