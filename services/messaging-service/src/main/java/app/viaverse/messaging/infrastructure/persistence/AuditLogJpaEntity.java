package app.viaverse.messaging.infrastructure.persistence;

import app.viaverse.observability.audit.AuditAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogJpaEntity {
    @Id
    private UUID id;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "actor_type", nullable = false, length = 64)
    private String actorType;

    @Column(name = "actor_id", nullable = false, length = 160)
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 80)
    private AuditAction action;

    @Column(name = "resource_type", length = 160)
    private String resourceType;

    @Column(name = "resource_id", length = 160)
    private String resourceId;

    @Column(name = "correlation_id", length = 160)
    private String correlationId;

    @Column(name = "request_id", length = 160)
    private String requestId;

    @Column(name = "source", length = 160)
    private String source;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata_json", nullable = false, columnDefinition = "jsonb")
    private String metadataJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuditLogJpaEntity() {
    }

    public AuditLogJpaEntity(
            UUID id,
            Instant occurredAt,
            String actorType,
            String actorId,
            AuditAction action,
            String resourceType,
            String resourceId,
            String correlationId,
            String requestId,
            String source,
            String metadataJson,
            Instant createdAt
    ) {
        this.id = id;
        this.occurredAt = occurredAt;
        this.actorType = actorType;
        this.actorId = actorId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.correlationId = correlationId;
        this.requestId = requestId;
        this.source = source;
        this.metadataJson = metadataJson;
        this.createdAt = createdAt;
    }
}

