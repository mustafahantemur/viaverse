package app.viaverse.media.asset.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media_upload_session")
public class MediaUploadSessionJpaEntity {
    @Id
    private UUID id;
    @Column(name = "asset_id", nullable = false, unique = true)
    private UUID assetId;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    @Column(nullable = false)
    private long version;

    protected MediaUploadSessionJpaEntity() {
    }

    public MediaUploadSessionJpaEntity(
            UUID id,
            UUID assetId,
            Instant expiresAt,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = id;
        this.assetId = assetId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public UUID getId() { return id; }
    public UUID getAssetId() { return assetId; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
