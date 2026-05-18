package app.viaverse.content.post.infrastructure.adapter.out.persistence.entity;

import app.viaverse.content.post.domain.enums.ContentAuthorModeEnum;
import app.viaverse.content.post.domain.enums.ContentModerationStatusEnum;
import app.viaverse.content.post.domain.enums.ContentPostStatusEnum;
import app.viaverse.content.post.domain.enums.ContentPostTypeEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "content_post")
public class ContentPostJpaEntity {

    @Id
    private UUID id;
    @Column(name = "author_account_id", nullable = false)
    private UUID authorAccountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "author_mode", nullable = false, length = 32)
    private ContentAuthorModeEnum authorMode;
    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 32)
    private ContentPostTypeEnum postType;
    @Column(length = 160)
    private String title;
    @Column(nullable = false, length = 4000)
    private String body;
    @Column(length = 120)
    private String city;
    @Column(length = 120)
    private String district;
    @Column(name = "event_starts_at")
    private Instant eventStartsAt;
    @Column(name = "event_ends_at")
    private Instant eventEndsAt;
    @ElementCollection
    @CollectionTable(name = "content_post_media", joinColumns = @JoinColumn(name = "post_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "media_asset_id", nullable = false)
    private List<UUID> mediaAssetIds = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ContentPostStatusEnum status;
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, length = 32)
    private ContentModerationStatusEnum moderationStatus;
    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    @Column(nullable = false)
    private long version;

    protected ContentPostJpaEntity() {
    }

    public ContentPostJpaEntity(
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
        this.id = id;
        this.authorAccountId = authorAccountId;
        this.authorMode = authorMode;
        this.postType = postType;
        this.title = title;
        this.body = body;
        this.city = city;
        this.district = district;
        this.eventStartsAt = eventStartsAt;
        this.eventEndsAt = eventEndsAt;
        this.mediaAssetIds = new ArrayList<>(mediaAssetIds);
        this.status = status;
        this.moderationStatus = moderationStatus;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
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
}
