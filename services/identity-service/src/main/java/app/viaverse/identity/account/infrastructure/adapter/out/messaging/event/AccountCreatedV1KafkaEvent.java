package app.viaverse.identity.account.infrastructure.adapter.out.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record AccountCreatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        String displayName
) {
}
