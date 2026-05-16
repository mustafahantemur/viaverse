package app.viaverse.identity.account.infrastructure.adapter.out.messaging.event;

import app.viaverse.identity.account.domain.AccountStatusEnum;
import java.time.Instant;
import java.util.UUID;

public record AccountStatusChangedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        AccountStatusEnum newStatus
) {
}
