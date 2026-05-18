package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import java.util.List;

public interface ListOpenServiceRequestsUseCase {

    List<ServiceRequest> execute();
}
