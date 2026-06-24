package app.viaverse.trustgamification.trust.application.port.out;

import app.viaverse.trustgamification.trust.domain.model.TrustState;

public interface TrustScoreEventPublisher {

    void publishUpdated(TrustState trustState);
}
