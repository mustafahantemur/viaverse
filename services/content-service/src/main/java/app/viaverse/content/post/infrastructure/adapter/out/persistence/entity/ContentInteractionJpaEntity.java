package app.viaverse.content.post.infrastructure.adapter.out.persistence.entity;

import app.viaverse.content.post.domain.enums.ContentSignalTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content_interaction")
public class ContentInteractionJpaEntity {
    @Id
    private UUID id;
    @Column(name = "viewer_account_id", nullable = false)
    private UUID viewerAccountId;
    @Column(name = "post_id", nullable = false)
    private UUID postId;
    @Enumerated(EnumType.STRING)
    @Column(name = "signal_type", nullable = false, length = 32)
    private ContentSignalTypeEnum signalType;
    @Column(nullable = false, length = 80)
    private String surface;
    @Column
    private Integer position;
    @Column(name = "dwell_time_ms")
    private Long dwellTimeMs;
    @Column(name = "session_id")
    private UUID sessionId;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    @Column(nullable = false)
    private long version;

    protected ContentInteractionJpaEntity() {
    }

    public ContentInteractionJpaEntity(
            UUID id,
            UUID viewerAccountId,
            UUID postId,
            ContentSignalTypeEnum signalType,
            String surface,
            Integer position,
            Long dwellTimeMs,
            UUID sessionId,
            Instant occurredAt,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = id;
        this.viewerAccountId = viewerAccountId;
        this.postId = postId;
        this.signalType = signalType;
        this.surface = surface;
        this.position = position;
        this.dwellTimeMs = dwellTimeMs;
        this.sessionId = sessionId;
        this.occurredAt = occurredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public UUID getId() { return id; }
    public UUID getViewerAccountId() { return viewerAccountId; }
    public UUID getPostId() { return postId; }
    public ContentSignalTypeEnum getSignalType() { return signalType; }
    public String getSurface() { return surface; }
    public Integer getPosition() { return position; }
    public Long getDwellTimeMs() { return dwellTimeMs; }
    public UUID getSessionId() { return sessionId; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
