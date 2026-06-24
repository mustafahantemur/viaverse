package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.SubmitOfferUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.application.port.out.OfferRepository;
import app.viaverse.marketplace.marketplace.application.port.out.ProviderEligibilityGateway;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.shared.kernel.error.ConflictException;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmitOfferUseCaseImpl implements SubmitOfferUseCase {

    private final ServiceRequestRepository requestRepository;
    private final OfferRepository offerRepository;
    private final ProviderEligibilityGateway providerEligibilityGateway;
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public SubmitOfferUseCaseImpl(
            ServiceRequestRepository requestRepository,
            OfferRepository offerRepository,
            ProviderEligibilityGateway providerEligibilityGateway,
            MarketplaceEventPublisher eventPublisher,
            Clock clock
    ) {
        this.requestRepository = requestRepository;
        this.offerRepository = offerRepository;
        this.providerEligibilityGateway = providerEligibilityGateway;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("marketplace.offer.submit")
    @Transactional
    public Offer execute(Command command) {
        var request = requestRepository.findById(command.requestId())
                .orElseThrow(() -> new NotFoundException("Service request not found"));
        if (!request.isOpen()) {
            throw new ConflictException("Only open requests can receive offers");
        }
        if (request.getRequesterAccountId().equals(command.providerAccountId())) {
            throw new ConflictException("Requester cannot submit an offer to their own request");
        }
        if (!providerEligibilityGateway.getEligibility(command.providerAccountId()).canOffer()) {
            throw new ForbiddenException("Provider capability is required to submit an offer");
        }
        if (offerRepository.findByRequestIdAndProviderAccountId(
                command.requestId(),
                command.providerAccountId()
        ).isPresent()) {
            throw new ConflictException("Provider already submitted an offer for this request");
        }
        Offer saved = offerRepository.save(Offer.submit(
                command.requestId(),
                command.providerAccountId(),
                command.amountMinor(),
                command.currency(),
                command.message(),
                clock.instant()
        ));
        eventPublisher.publishOfferSubmitted(saved);
        return saved;
    }
}
