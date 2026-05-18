package app.viaverse.marketplace.marketplace.application.usecase;

import app.viaverse.marketplace.marketplace.application.port.in.ListOffersForRequestUseCase;
import app.viaverse.marketplace.marketplace.application.port.out.OfferRepository;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListOffersForRequestUseCaseImpl implements ListOffersForRequestUseCase {

    private final ServiceRequestRepository requestRepository;
    private final OfferRepository offerRepository;

    public ListOffersForRequestUseCaseImpl(
            ServiceRequestRepository requestRepository,
            OfferRepository offerRepository
    ) {
        this.requestRepository = requestRepository;
        this.offerRepository = offerRepository;
    }

    @Override
    @ObservedAction("marketplace.offer.list_for_request")
    public List<Offer> execute(Command command) {
        var request = requestRepository.findById(command.requestId())
                .orElseThrow(() -> new NotFoundException("Service request not found"));
        if (!request.getRequesterAccountId().equals(command.requesterAccountId())) {
            throw new ForbiddenException("Only the requester can inspect offers");
        }
        return offerRepository.findAllByRequestId(command.requestId());
    }
}
