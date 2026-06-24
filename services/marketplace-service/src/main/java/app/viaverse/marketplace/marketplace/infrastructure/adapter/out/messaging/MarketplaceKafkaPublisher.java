package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.messaging;

import app.viaverse.contracts.marketplace.MarketplaceEventTypes;
import app.viaverse.contracts.marketplace.MarketplaceJobCompletedV1KafkaEvent;
import app.viaverse.contracts.marketplace.MarketplaceJobCreatedV1KafkaEvent;
import app.viaverse.contracts.marketplace.MarketplaceJobStartedV1KafkaEvent;
import app.viaverse.contracts.marketplace.MarketplaceOfferAcceptedV1KafkaEvent;
import app.viaverse.contracts.marketplace.MarketplaceOfferWithdrawnV1KafkaEvent;
import app.viaverse.contracts.marketplace.MarketplaceOfferSubmittedV1KafkaEvent;
import app.viaverse.contracts.marketplace.MarketplaceRequestCancelledV1KafkaEvent;
import app.viaverse.contracts.marketplace.MarketplaceRequestCreatedV1KafkaEvent;
import app.viaverse.marketplace.marketplace.application.port.out.MarketplaceEventPublisher;
import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.messaging.outbox.OutboxEventWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MarketplaceKafkaPublisher implements MarketplaceEventPublisher {

    private static final String BINDING_NAME = "marketplaceEvents-out-0";

    private final OutboxEventWriter outboxWriter;
    private final Clock clock;

    public MarketplaceKafkaPublisher(OutboxEventWriter outboxWriter, Clock clock) {
        this.outboxWriter = outboxWriter;
        this.clock = clock;
    }

    @Override
    public void publishRequestCreated(ServiceRequest request) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.REQUEST_CREATED_V1,
                BINDING_NAME,
                request.getId().toString(),
                new MarketplaceRequestCreatedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        request.getId(),
                        request.getRequesterAccountId(),
                        request.getCategory().name(),
                        request.getCity(),
                        request.getDistrict()
                )
        );
    }

    @Override
    public void publishOfferSubmitted(Offer offer) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.OFFER_SUBMITTED_V1,
                BINDING_NAME,
                offer.getRequestId().toString(),
                new MarketplaceOfferSubmittedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        offer.getId(),
                        offer.getRequestId(),
                        offer.getProviderAccountId(),
                        offer.getAmountMinor(),
                        offer.getCurrency()
                )
        );
    }

    @Override
    public void publishRequestCancelled(ServiceRequest request) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.REQUEST_CANCELLED_V1,
                BINDING_NAME,
                request.getId().toString(),
                new MarketplaceRequestCancelledV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        request.getId(),
                        request.getRequesterAccountId()
                )
        );
    }

    @Override
    public void publishOfferWithdrawn(Offer offer) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.OFFER_WITHDRAWN_V1,
                BINDING_NAME,
                offer.getId().toString(),
                new MarketplaceOfferWithdrawnV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        offer.getId(),
                        offer.getRequestId(),
                        offer.getProviderAccountId()
                )
        );
    }

    @Override
    public void publishOfferAccepted(ServiceRequest request, Offer offer) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.OFFER_ACCEPTED_V1,
                BINDING_NAME,
                request.getId().toString(),
                new MarketplaceOfferAcceptedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        offer.getId(),
                        request.getId(),
                        request.getRequesterAccountId(),
                        offer.getProviderAccountId()
                )
        );
    }

    @Override
    public void publishJobCreated(Job job) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.JOB_CREATED_V1,
                BINDING_NAME,
                job.getId().toString(),
                new MarketplaceJobCreatedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        job.getId(),
                        job.getRequestId(),
                        job.getAcceptedOfferId(),
                        job.getRequesterAccountId(),
                        job.getProviderAccountId(),
                        job.getAgreedAmountMinor(),
                        job.getCurrency()
                )
        );
    }

    @Override
    public void publishJobStarted(Job job) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.JOB_STARTED_V1,
                BINDING_NAME,
                job.getId().toString(),
                new MarketplaceJobStartedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        job.getId(),
                        job.getRequesterAccountId(),
                        job.getProviderAccountId()
                )
        );
    }

    @Override
    public void publishJobCompleted(Job job) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MarketplaceEventTypes.JOB_COMPLETED_V1,
                BINDING_NAME,
                job.getId().toString(),
                new MarketplaceJobCompletedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        job.getId(),
                        job.getRequesterAccountId(),
                        job.getProviderAccountId()
                )
        );
    }
}
