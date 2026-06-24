package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import java.util.List;
import java.util.UUID;

public interface ListRelevantServiceRequestsUseCase {

    List<ServiceRequest> execute(UUID accountId);
}
