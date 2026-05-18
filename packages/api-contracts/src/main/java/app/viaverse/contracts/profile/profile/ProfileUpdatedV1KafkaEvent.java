package app.viaverse.contracts.profile.profile;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted after profile-service changes user-facing display fields.
 */
public record ProfileUpdatedV1KafkaEvent(
        UUID eventId,
        Instant occurredAt,
        String version,
        UUID accountId,
        String displayName,
        String firstName,
        String lastName,
        UUID avatarMediaId,
        String headline,
        String publicVisibility
) {
}
