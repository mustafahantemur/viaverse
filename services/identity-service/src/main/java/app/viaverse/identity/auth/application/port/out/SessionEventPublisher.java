package app.viaverse.identity.auth.application.port.out;

import java.util.UUID;

public interface SessionEventPublisher {

    void publishRevoked(UUID accountId, UUID sessionId);
}
