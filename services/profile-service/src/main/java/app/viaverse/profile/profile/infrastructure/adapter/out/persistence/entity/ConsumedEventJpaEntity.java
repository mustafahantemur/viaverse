package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consumed_event")
public class ConsumedEventJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 160)
    private String eventType;

    @Column(name = "consumed_at", nullable = false)
    private Instant consumedAt;

    protected ConsumedEventJpaEntity() {
    }

    public ConsumedEventJpaEntity(
            UUID eventId,
            String eventType,
            Instant consumedAt,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.eventId = eventId;
        this.eventType = eventType;
        this.consumedAt = consumedAt;
    }

    public ConsumedEventJpaEntity(UUID eventId, String eventType, Instant consumedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.consumedAt = consumedAt;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getConsumedAt() {
        return consumedAt;
    }
}
