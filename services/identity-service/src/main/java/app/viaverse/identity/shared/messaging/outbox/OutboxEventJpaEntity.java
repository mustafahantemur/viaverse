package app.viaverse.identity.shared.messaging.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "outbox_event")
public class OutboxEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 160)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_json", nullable = false, columnDefinition = "jsonb")
    private String payloadJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "headers_json", nullable = false, columnDefinition = "jsonb")
    private String headersJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OutboxEventStatusEnum status;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OutboxEventJpaEntity() {
    }

    public OutboxEventJpaEntity(
            UUID id,
            String eventType,
            String payloadJson,
            String headersJson,
            OutboxEventStatusEnum status,
            Instant occurredAt,
            Instant availableAt,
            Instant now
    ) {
        this.id = id;
        this.eventType = eventType;
        this.payloadJson = payloadJson;
        this.headersJson = headersJson;
        this.status = status;
        this.occurredAt = occurredAt;
        this.availableAt = availableAt;
        this.attempts = 0;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() { return id; }
    public String getEventType() { return eventType; }
    public String getPayloadJson() { return payloadJson; }
    public String getHeadersJson() { return headersJson; }
    public OutboxEventStatusEnum getStatus() { return status; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getAvailableAt() { return availableAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public int getAttempts() { return attempts; }
    public String getLastError() { return lastError; }

    public void markSent(Instant now) {
        this.status = OutboxEventStatusEnum.SENT;
        this.publishedAt = now;
        this.updatedAt = now;
        this.lastError = null;
    }

    public void markFailureAndReschedule(String error, Instant nextAvailableAt, Instant now) {
        this.attempts = this.attempts + 1;
        this.lastError = error;
        this.availableAt = nextAvailableAt;
        this.updatedAt = now;
    }

    public void markFailedTerminally(String error, Instant now) {
        this.attempts = this.attempts + 1;
        this.status = OutboxEventStatusEnum.FAILED;
        this.lastError = error;
        this.updatedAt = now;
    }
}
