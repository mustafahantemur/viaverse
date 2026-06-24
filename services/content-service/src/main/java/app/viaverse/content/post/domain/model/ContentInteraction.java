package app.viaverse.content.post.domain.model;

import app.viaverse.content.post.domain.enums.ContentSignalTypeEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ContentInteraction {
    private final UUID id;
    private final UUID viewerAccountId;
    private final UUID postId;
    private final ContentSignalTypeEnum signalType;
    private final String surface;
    private final Integer position;
    private final Long dwellTimeMs;
    private final UUID sessionId;
    private final Instant occurredAt;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public ContentInteraction(
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
        this.id = Objects.requireNonNull(id, "id");
        this.viewerAccountId = Objects.requireNonNull(viewerAccountId, "viewerAccountId");
        this.postId = Objects.requireNonNull(postId, "postId");
        this.signalType = Objects.requireNonNull(signalType, "signalType");
        this.surface = requireText(surface, "surface", 80);
        this.position = optionalNonNegative(position, "position");
        this.dwellTimeMs = optionalNonNegative(dwellTimeMs, "dwellTimeMs");
        this.sessionId = sessionId;
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static ContentInteraction record(
            UUID viewerAccountId,
            UUID postId,
            ContentSignalTypeEnum signalType,
            String surface,
            Integer position,
            Long dwellTimeMs,
            UUID sessionId,
            Instant occurredAt,
            Instant now
    ) {
        return new ContentInteraction(
                UUID.randomUUID(),
                viewerAccountId,
                postId,
                signalType,
                surface,
                position,
                dwellTimeMs,
                sessionId,
                occurredAt,
                now,
                now,
                0
        );
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

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }

    private static Integer optionalNonNegative(Integer value, String field) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(field + " must be non-negative");
        }
        return value;
    }

    private static Long optionalNonNegative(Long value, String field) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(field + " must be non-negative");
        }
        return value;
    }
}
