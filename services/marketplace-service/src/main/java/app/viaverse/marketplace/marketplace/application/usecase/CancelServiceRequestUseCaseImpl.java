package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.CancelServiceRequestUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.application.port.out.OfferRepository;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.shared.kernel.error.ConflictException;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelServiceRequestUseCaseImpl implements CancelServiceRequestUseCase {
    private final ServiceRequestRepository requestRepository;
    private final OfferRepository offerRepository;
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public CancelServiceRequestUseCaseImpl(
            ServiceRequestRepository requestRepository,
            OfferRepository offerRepository,
            MarketplaceEventPublisher eventPublisher,
            Clock clock
    ) {
        this.requestRepository = requestRepository;
        this.offerRepository = offerRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("marketplace.request.cancel")
    @Transactional
    public ServiceRequest execute(Command command) {
        ServiceRequest current = requestRepository.findById(command.requestId())
                .orElseThrow(() -> new NotFoundException("Service request not found"));
        if (!current.getRequesterAccountId().equals(command.requesterAccountId())) {
            throw new ForbiddenException("Only the requester can cancel the request");
        }
        try {
            var now = clock.instant();
            ServiceRequest cancelled = requestRepository.save(current.cancel(now));
            offerRepository.saveAll(offerRepository.findAllByRequestId(command.requestId()).stream()
                    .map(offer -> offer.isSubmitted() ? offer.reject(now) : offer)
                    .toList());
            eventPublisher.publishRequestCancelled(cancelled);
            return cancelled;
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
    }
}
