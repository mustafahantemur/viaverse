package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.ListCurrentOffersUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.OfferRepository;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListCurrentOffersUseCaseImpl implements ListCurrentOffersUseCase {
    private final OfferRepository repository;

    public ListCurrentOffersUseCaseImpl(OfferRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("marketplace.offer.list_current")
    public List<Offer> execute(UUID providerAccountId) {
        return repository.findAllByProviderAccountId(providerAccountId);
    }
}
