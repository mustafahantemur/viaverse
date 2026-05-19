package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.JobJpaEntity;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.JobTimelineEntryJpaEntity;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.OfferJpaEntity;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.ServiceRequestJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketplaceJpaMapper {

    public ServiceRequestJpaEntity toEntity(ServiceRequest request) {
        return new ServiceRequestJpaEntity(
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
                request.getUpdatedAt(),
                request.getVersion()
        );
    }

    public ServiceRequest toDomain(ServiceRequestJpaEntity entity) {
        return new ServiceRequest(
                entity.getId(),
                entity.getRequesterAccountId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getBudgetMinAmountMinor(),
                entity.getBudgetMaxAmountMinor(),
                entity.getCurrency(),
                entity.isRemoteAllowed(),
                entity.getDistrict(),
                entity.getCity(),
                entity.getMediaAssetIds(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public OfferJpaEntity toEntity(Offer offer) {
        return new OfferJpaEntity(
                offer.getId(),
                offer.getRequestId(),
                offer.getProviderAccountId(),
                offer.getAmountMinor(),
                offer.getCurrency(),
                offer.getMessage(),
                offer.getStatus(),
                offer.getCreatedAt(),
                offer.getUpdatedAt(),
                offer.getVersion()
        );
    }

    public Offer toDomain(OfferJpaEntity entity) {
        return new Offer(
                entity.getId(),
                entity.getRequestId(),
                entity.getProviderAccountId(),
                entity.getAmountMinor(),
                entity.getCurrency(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public JobJpaEntity toEntity(Job job) {
        return new JobJpaEntity(
                job.getId(),
                job.getRequestId(),
                job.getAcceptedOfferId(),
                job.getRequesterAccountId(),
                job.getProviderAccountId(),
                job.getAgreedAmountMinor(),
                job.getCurrency(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getVersion()
        );
    }

    public Job toDomain(JobJpaEntity entity) {
        return new Job(
                entity.getId(),
                entity.getRequestId(),
                entity.getAcceptedOfferId(),
                entity.getRequesterAccountId(),
                entity.getProviderAccountId(),
                entity.getAgreedAmountMinor(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public JobTimelineEntryJpaEntity toEntity(JobTimelineEntry entry) {
        return new JobTimelineEntryJpaEntity(
                entry.getId(),
                entry.getJobId(),
                entry.getActorAccountId(),
                entry.getEventType(),
                entry.getMessage(),
                entry.getOccurredAt(),
                entry.getCreatedAt()
        );
    }

    public JobTimelineEntry toDomain(JobTimelineEntryJpaEntity entity) {
        return new JobTimelineEntry(
                entity.getId(),
                entity.getJobId(),
                entity.getActorAccountId(),
                entity.getEventType(),
                entity.getMessage(),
                entity.getOccurredAt(),
                entity.getCreatedAt()
        );
    }
}
