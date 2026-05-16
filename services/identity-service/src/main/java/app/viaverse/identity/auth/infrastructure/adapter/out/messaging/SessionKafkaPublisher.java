package app.viaverse.identity.auth.infrastructure.adapter.out.messaging;

import app.viaverse.identity.auth.application.port.out.SessionEventPublisher;
import app.viaverse.identity.auth.infrastructure.adapter.out.messaging.event.SessionRevokedV1KafkaEvent;
import app.viaverse.identity.shared.messaging.outbox.OutboxEventWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SessionKafkaPublisher implements SessionEventPublisher {

    private static final String BINDING_NAME = "identitySessionEvents-out-0";
    private static final String EVENT_TYPE_REVOKED = "session.SessionRevoked.v1";

    private final OutboxEventWriter outboxWriter;
    private final Clock clock;

    public SessionKafkaPublisher(OutboxEventWriter outboxWriter, Clock clock) {
        this.outboxWriter = outboxWriter;
        this.clock = clock;
    }

    @Override
    public void publishRevoked(UUID accountId, UUID sessionId) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                EVENT_TYPE_REVOKED,
                BINDING_NAME,
                accountId.toString(),
                new SessionRevokedV1KafkaEvent(eventId, Instant.now(clock), "v1", accountId, sessionId)
        );
    }
}
