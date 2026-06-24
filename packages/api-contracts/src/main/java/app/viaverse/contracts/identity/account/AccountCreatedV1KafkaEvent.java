package app.viaverse.contracts.identity.account;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted when identity-service creates a new account.
 */
public record AccountCreatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        String displayName,
        String firstName,
        String lastName
) {
}
