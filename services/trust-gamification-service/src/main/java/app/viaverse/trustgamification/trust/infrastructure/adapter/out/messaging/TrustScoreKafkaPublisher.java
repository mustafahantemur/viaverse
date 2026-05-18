package app.viaverse.trustgamification.trust.infrastructure.adapter.out.messaging;

import app.viaverse.contracts.trust.score.TrustEventTypes;
import app.viaverse.contracts.trust.score.TrustScoreUpdatedV1KafkaEvent;
import app.viaverse.messaging.outbox.OutboxEventWriter;
import app.viaverse.trustgamification.trust.application.port.out.TrustScoreEventPublisher;
import app.viaverse.trustgamification.trust.domain.model.TrustState;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TrustScoreKafkaPublisher implements TrustScoreEventPublisher {

    private static final String BINDING_NAME = "trustScoreEvents-out-0";

    private final OutboxEventWriter outboxWriter;
    private final Clock clock;

    public TrustScoreKafkaPublisher(OutboxEventWriter outboxWriter, Clock clock) {
        this.outboxWriter = outboxWriter;
        this.clock = clock;
    }

    @Override
    public void publishUpdated(TrustState trustState) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                TrustEventTypes.TRUST_SCORE_UPDATED_V1,
                BINDING_NAME,
                trustState.getAccountId().toString(),
                new TrustScoreUpdatedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        trustState.getAccountId(),
                        trustState.getScore(),
                        trustState.getLevel().name(),
                        trustState.getBadge().name()
                )
        );
    }
}
