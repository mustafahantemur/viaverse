package app.viaverse.marketplace.marketplace.application.port.out;

import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;

public interface MarketplaceEventPublisher {

    void publishRequestCreated(ServiceRequest request);

    void publishOfferSubmitted(Offer offer);

    void publishOfferAccepted(ServiceRequest request, Offer offer);

    void publishJobCreated(Job job);

    void publishJobStarted(Job job);

    void publishJobCompleted(Job job);
}
