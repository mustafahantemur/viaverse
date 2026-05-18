package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.AcceptOfferUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.JobRepository;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.application.port.out.OfferRepository;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.shared.kernel.error.ConflictException;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcceptOfferUseCaseImpl implements AcceptOfferUseCase {

    private final ServiceRequestRepository requestRepository;
    private final OfferRepository offerRepository;
    private final JobRepository jobRepository;
    private final MarketplaceEventPublisher eventPublisher;
    private final Clock clock;

    public AcceptOfferUseCaseImpl(
            ServiceRequestRepository requestRepository,
            OfferRepository offerRepository,
            JobRepository jobRepository,
            MarketplaceEventPublisher eventPublisher,
            Clock clock
    ) {
        this.requestRepository = requestRepository;
        this.offerRepository = offerRepository;
        this.jobRepository = jobRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("marketplace.offer.accept")
    @Transactional
    public Job execute(Command command) {
        var request = requestRepository.findById(command.requestId())
                .orElseThrow(() -> new NotFoundException("Service request not found"));
        if (!request.getRequesterAccountId().equals(command.requesterAccountId())) {
            throw new ForbiddenException("Only the requester can accept an offer");
        }
        if (!request.isOpen()) {
            throw new ConflictException("Only open requests can accept offers");
        }
        Offer selected = offerRepository.findById(command.offerId())
                .filter(offer -> offer.getRequestId().equals(command.requestId()))
                .orElseThrow(() -> new NotFoundException("Offer not found for request"));
        if (!selected.isSubmitted()) {
            throw new ConflictException("Only submitted offers can be accepted");
        }

        Instant now = clock.instant();
        Offer accepted = selected.accept(now);
        List<Offer> updatedOffers = offerRepository.findAllByRequestId(command.requestId()).stream()
                .map(offer -> offer.getId().equals(accepted.getId()) ? accepted : offer.isSubmitted() ? offer.reject(now) : offer)
                .toList();
        offerRepository.saveAll(updatedOffers);
        var matchedRequest = requestRepository.save(request.markMatched(now));
        Job job = jobRepository.save(Job.create(matchedRequest, accepted, now));
        eventPublisher.publishOfferAccepted(matchedRequest, accepted);
        eventPublisher.publishJobCreated(job);
        return job;
    }
}
