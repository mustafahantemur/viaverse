package app.viaverse.marketplace.marketplace.application.port.out;

import app.viaverse.marketplace.marketplace.domain.model.Offer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferRepository {

    Offer save(Offer offer);

    List<Offer> saveAll(List<Offer> offers);

    Optional<Offer> findById(UUID offerId);

    Optional<Offer> findByRequestIdAndProviderAccountId(UUID requestId, UUID providerAccountId);

    List<Offer> findAllByRequestId(UUID requestId);

    List<Offer> findAllByProviderAccountId(UUID providerAccountId);
}
