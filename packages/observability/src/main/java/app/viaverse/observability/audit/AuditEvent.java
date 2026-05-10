package app.viaverse.observability.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record AuditEvent(
        UUID id,
        Instant occurredAt,
        AuditActor actor,
        AuditAction action,
        String resourceType,
        String resourceId,
        AuditContext context,
        Map<String, String> metadata
) {
    public AuditEvent {
        id = id == null ? UUID.randomUUID() : id;
        occurredAt = Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        actor = Objects.requireNonNull(actor, "actor must not be null");
        action = Objects.requireNonNull(action, "action must not be null");
        resourceType = normalize(resourceType);
        resourceId = normalize(resourceId);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

