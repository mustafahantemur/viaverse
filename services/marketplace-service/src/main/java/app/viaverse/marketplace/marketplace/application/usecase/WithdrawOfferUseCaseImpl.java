package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.WithdrawOfferUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.application.port.out.OfferRepository;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.shared.kernel.error.ConflictException;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WithdrawOfferUseCaseImpl implements WithdrawOfferUseCase {
    private final OfferRepository repository;
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public WithdrawOfferUseCaseImpl(
            OfferRepository repository,
            MarketplaceEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("marketplace.offer.withdraw")
    @Transactional
    public Offer execute(Command command) {
        Offer current = repository.findById(command.offerId())
                .orElseThrow(() -> new NotFoundException("Offer not found"));
        if (!current.getProviderAccountId().equals(command.providerAccountId())) {
            throw new ForbiddenException("Only the provider can withdraw the offer");
        }
        try {
            Offer saved = repository.save(current.withdraw(clock.instant()));
            eventPublisher.publishOfferWithdrawn(saved);
            return saved;
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
    }
}
