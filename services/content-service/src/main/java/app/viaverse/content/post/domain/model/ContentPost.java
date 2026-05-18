package app.viaverse.content.post.domain.model;

import app.viaverse.content.post.domain.enums.ContentAuthorModeEnum;
import app.viaverse.content.post.domain.enums.ContentModerationStatusEnum;
import app.viaverse.content.post.domain.enums.ContentPostStatusEnum;
import app.viaverse.content.post.domain.enums.ContentPostTypeEnum;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ContentPost {

    private final UUID id;
    private final UUID authorAccountId;
    private final ContentAuthorModeEnum authorMode;
    private final ContentPostTypeEnum postType;
    private final String title;
    private final String body;
    private final String city;
    private final String district;
    private final Instant eventStartsAt;
    private final Instant eventEndsAt;
    private final List<UUID> mediaAssetIds;
    private final ContentPostStatusEnum status;
    private final ContentModerationStatusEnum moderationStatus;
    private final Instant publishedAt;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public ContentPost(
            UUID id,
            UUID authorAccountId,
            ContentAuthorModeEnum authorMode,
            ContentPostTypeEnum postType,
            String title,
            String body,
            String city,
            String district,
            Instant eventStartsAt,
            Instant eventEndsAt,
            List<UUID> mediaAssetIds,
            ContentPostStatusEnum status,
            ContentModerationStatusEnum moderationStatus,
            Instant publishedAt,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.authorAccountId = Objects.requireNonNull(authorAccountId, "authorAccountId");
        this.authorMode = Objects.requireNonNull(authorMode, "authorMode");
        this.postType = Objects.requireNonNull(postType, "postType");
        this.title = optionalText(title, "title", 160);
        this.body = requireText(body, "body", 4000);
        this.city = optionalText(city, "city", 120);
        this.district = optionalText(district, "district", 120);
        this.eventStartsAt = eventStartsAt;
        this.eventEndsAt = eventEndsAt;
        this.mediaAssetIds = List.copyOf(mediaAssetIds == null ? List.of() : mediaAssetIds);
        this.status = Objects.requireNonNull(status, "status");
        this.moderationStatus = Objects.requireNonNull(moderationStatus, "moderationStatus");
        this.publishedAt = Objects.requireNonNull(publishedAt, "publishedAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
        validateEventWindow();
        validateTypeSpecificFields();
    }

    public static ContentPost publish(
            UUID authorAccountId,
            ContentAuthorModeEnum authorMode,
            ContentPostTypeEnum postType,
            String title,
            String body,
            String city,
            String district,
            Instant eventStartsAt,
            Instant eventEndsAt,
            List<UUID> mediaAssetIds,
            Instant now
    ) {
        return new ContentPost(
                UUID.randomUUID(),
                authorAccountId,
                authorMode,
                postType,
                title,
                body,
                city,
                district,
                eventStartsAt,
                eventEndsAt,
                mediaAssetIds,
                ContentPostStatusEnum.PUBLISHED,
                ContentModerationStatusEnum.AUTO_APPROVED,
                now,
                now,
                now,
                0
        );
    }

    public UUID getId() { return id; }
    public UUID getAuthorAccountId() { return authorAccountId; }
    public ContentAuthorModeEnum getAuthorMode() { return authorMode; }
    public ContentPostTypeEnum getPostType() { return postType; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public Instant getEventStartsAt() { return eventStartsAt; }
    public Instant getEventEndsAt() { return eventEndsAt; }
    public List<UUID> getMediaAssetIds() { return mediaAssetIds; }
    public ContentPostStatusEnum getStatus() { return status; }
    public ContentModerationStatusEnum getModerationStatus() { return moderationStatus; }
    public Instant getPublishedAt() { return publishedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }

    private void validateEventWindow() {
        if (eventStartsAt != null && eventEndsAt != null && eventStartsAt.isAfter(eventEndsAt)) {
            throw new IllegalArgumentException("eventStartsAt must not be after eventEndsAt");
        }
    }

    private void validateTypeSpecificFields() {
        if (postType == ContentPostTypeEnum.EVENT && eventStartsAt == null) {
            throw new IllegalArgumentException("EVENT posts require eventStartsAt");
        }
        if (postType != ContentPostTypeEnum.EVENT && (eventStartsAt != null || eventEndsAt != null)) {
            throw new IllegalArgumentException("Only EVENT posts may contain event timing");
        }
        if (postType == ContentPostTypeEnum.BUSINESS_PROMOTION && authorMode != ContentAuthorModeEnum.BUSINESS) {
            throw new IllegalArgumentException("BUSINESS_PROMOTION posts require BUSINESS author mode");
        }
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }

    private static String optionalText(String value, String field, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }
}
