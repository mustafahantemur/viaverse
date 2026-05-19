package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.mapper;

import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.JobResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.JobTimelineEntryResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.OfferResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.ServiceRequestResponse;
import org.springframework.stereotype.Component;

@Component
public class MarketplaceDtoMapper {

    public ServiceRequestResponse toResponse(ServiceRequest request) {
        return new ServiceRequestResponse(
                request.getId(),
                request.getRequesterAccountId(),
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getBudgetMinAmountMinor(),
                request.getBudgetMaxAmountMinor(),
                request.getCurrency(),
                request.isRemoteAllowed(),
                request.getDistrict(),
                request.getCity(),
                request.getMediaAssetIds(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    public OfferResponse toResponse(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getRequestId(),
                offer.getProviderAccountId(),
                offer.getAmountMinor(),
                offer.getCurrency(),
                offer.getMessage(),
                offer.getStatus(),
                offer.getCreatedAt(),
                offer.getUpdatedAt()
        );
    }

    public JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getRequestId(),
                job.getAcceptedOfferId(),
                job.getRequesterAccountId(),
                job.getProviderAccountId(),
                job.getAgreedAmountMinor(),
                job.getCurrency(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }

    public JobTimelineEntryResponse toResponse(JobTimelineEntry entry) {
        return new JobTimelineEntryResponse(
                entry.getId(),
                entry.getJobId(),
                entry.getActorAccountId(),
                entry.getEventType(),
                entry.getMessage(),
                entry.getOccurredAt(),
                entry.getCreatedAt()
        );
    }
}
