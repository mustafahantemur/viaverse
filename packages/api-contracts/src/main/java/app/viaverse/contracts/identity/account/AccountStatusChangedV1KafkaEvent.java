package app.viaverse.contracts.identity.account;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted when identity-service changes the credential lifecycle state of an account.
 */
public record AccountStatusChangedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        String newStatus
) {
}
