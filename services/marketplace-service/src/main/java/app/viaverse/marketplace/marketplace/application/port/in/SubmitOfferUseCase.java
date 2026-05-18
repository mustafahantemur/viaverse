package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.Offer;
import java.util.UUID;

public interface SubmitOfferUseCase {

    Offer execute(Command command);

    record Command(
            UUID requestId,
            UUID providerAccountId,
            long amountMinor,
            String currency,
            String message
    ) {
    }
}
