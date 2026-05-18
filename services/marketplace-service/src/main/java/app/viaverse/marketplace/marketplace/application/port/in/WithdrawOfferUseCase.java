package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.Offer;
import java.util.UUID;

public interface WithdrawOfferUseCase {
    Offer execute(Command command);

    record Command(UUID offerId, UUID providerAccountId) {
    }
}
