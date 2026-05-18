package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import java.util.UUID;

public interface CancelServiceRequestUseCase {
    ServiceRequest execute(Command command);

    record Command(UUID requestId, UUID requesterAccountId) {
    }
}
