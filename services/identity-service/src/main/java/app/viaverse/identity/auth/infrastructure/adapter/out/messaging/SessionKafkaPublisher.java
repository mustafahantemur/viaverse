package app.viaverse.identity.auth.infrastructure.adapter.out.messaging;

import app.viaverse.identity.auth.application.port.out.SessionEventPublisher;
import app.viaverse.identity.auth.infrastructure.adapter.out.messaging.event.SessionRevokedV1KafkaEvent;
import java.time.Instant;
import java.util.UUID;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class SessionKafkaPublisher implements SessionEventPublisher {

    private static final String BINDING_NAME = "identitySessionEvents-out-0";

    private final StreamBridge streamBridge;

    public SessionKafkaPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void publishRevoked(UUID accountId, UUID sessionId) {
        boolean accepted = streamBridge.send(BINDING_NAME, new SessionRevokedV1KafkaEvent(
                UUID.randomUUID(),
                Instant.now(),
                "v1",
                accountId,
                sessionId
        ));
        if (!accepted) {
            throw new IllegalStateException("Failed to publish session event");
        }
    }
}
