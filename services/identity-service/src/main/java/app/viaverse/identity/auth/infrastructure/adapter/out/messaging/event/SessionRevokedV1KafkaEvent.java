package app.viaverse.identity.auth.infrastructure.adapter.out.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record SessionRevokedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        UUID sessionId
) {
}
