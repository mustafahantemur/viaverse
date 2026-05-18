package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.Job;
import java.util.UUID;

public interface AcceptOfferUseCase {

    Job execute(Command command);

    record Command(UUID requestId, UUID offerId, UUID requesterAccountId) {
    }
}
