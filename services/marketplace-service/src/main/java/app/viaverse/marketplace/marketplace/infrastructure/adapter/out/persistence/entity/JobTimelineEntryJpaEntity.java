package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity;

import app.viaverse.marketplace.marketplace.domain.enums.JobTimelineEventTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_timeline_entry")
public class JobTimelineEntryJpaEntity {

    @Id
    private UUID id;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "actor_account_id")
    private UUID actorAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private JobTimelineEventTypeEnum eventType;

    @Column(length = 1000)
    private String message;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected JobTimelineEntryJpaEntity() {
    }

    public JobTimelineEntryJpaEntity(
            UUID id,
            UUID jobId,
            UUID actorAccountId,
            JobTimelineEventTypeEnum eventType,
            String message,
            Instant occurredAt,
            Instant createdAt
    ) {
        this.id = id;
        this.jobId = jobId;
        this.actorAccountId = actorAccountId;
        this.eventType = eventType;
        this.message = message;
        this.occurredAt = occurredAt;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getJobId() { return jobId; }
    public UUID getActorAccountId() { return actorAccountId; }
    public JobTimelineEventTypeEnum getEventType() { return eventType; }
    public String getMessage() { return message; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getCreatedAt() { return createdAt; }
}
