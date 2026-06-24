package app.viaverse.marketplace.marketplace.domain.model;

import app.viaverse.marketplace.marketplace.domain.enums.JobTimelineEventTypeEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class JobTimelineEntry {

    private final UUID id;
    private final UUID jobId;
    private final UUID actorAccountId;
    private final JobTimelineEventTypeEnum eventType;
    private final String message;
    private final Instant occurredAt;
    private final Instant createdAt;

    public JobTimelineEntry(
            UUID id,
            UUID jobId,
            UUID actorAccountId,
            JobTimelineEventTypeEnum eventType,
            String message,
            Instant occurredAt,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.jobId = Objects.requireNonNull(jobId, "jobId");
        this.actorAccountId = actorAccountId;
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.message = normalizeMessage(message, eventType);
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static JobTimelineEntry system(UUID jobId, JobTimelineEventTypeEnum eventType, Instant now) {
        return new JobTimelineEntry(UUID.randomUUID(), jobId, null, eventType, null, now, now);
    }

    public static JobTimelineEntry note(UUID jobId, UUID actorAccountId, String message, Instant now) {
        return new JobTimelineEntry(
                UUID.randomUUID(),
                jobId,
                Objects.requireNonNull(actorAccountId, "actorAccountId"),
                JobTimelineEventTypeEnum.NOTE_ADDED,
                message,
                now,
                now
        );
    }

    public UUID getId() { return id; }
    public UUID getJobId() { return jobId; }
    public UUID getActorAccountId() { return actorAccountId; }
    public JobTimelineEventTypeEnum getEventType() { return eventType; }
    public String getMessage() { return message; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getCreatedAt() { return createdAt; }

    private static String normalizeMessage(String value, JobTimelineEventTypeEnum eventType) {
        if (value == null || value.isBlank()) {
            if (eventType == JobTimelineEventTypeEnum.NOTE_ADDED) {
                throw new IllegalArgumentException("message is required for notes");
            }
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > 1000) {
            throw new IllegalArgumentException("message must not exceed 1000 characters");
        }
        return trimmed;
    }
}
