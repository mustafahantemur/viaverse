package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.Offer;
import java.util.List;
import java.util.UUID;

public interface ListCurrentOffersUseCase {
    List<Offer> execute(UUID providerAccountId);
}
