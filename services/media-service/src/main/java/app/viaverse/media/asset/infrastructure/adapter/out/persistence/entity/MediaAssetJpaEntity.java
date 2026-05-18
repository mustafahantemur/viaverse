package app.viaverse.media.asset.infrastructure.adapter.out.persistence.entity;

import app.viaverse.media.asset.domain.enums.MediaAssetKindEnum;
import app.viaverse.media.asset.domain.enums.MediaAssetStatusEnum;
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
@Table(name = "media_asset")
public class MediaAssetJpaEntity {
    @Id
    private UUID id;
    @Column(name = "owner_account_id", nullable = false)
    private UUID ownerAccountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_kind", nullable = false, length = 32)
    private MediaAssetKindEnum assetKind;
    @Column(name = "content_type", nullable = false, length = 160)
    private String contentType;
    @Column(name = "original_file_name", length = 255)
    private String originalFileName;
    @Column(name = "object_key", nullable = false, length = 255)
    private String objectKey;
    @Column(name = "byte_size")
    private Long byteSize;
    @Column(name = "checksum_sha256", length = 128)
    private String checksumSha256;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MediaAssetStatusEnum status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    @Column(nullable = false)
    private long version;

    protected MediaAssetJpaEntity() {
    }

    public MediaAssetJpaEntity(
            UUID id,
            UUID ownerAccountId,
            MediaAssetKindEnum assetKind,
            String contentType,
            String originalFileName,
            String objectKey,
            Long byteSize,
            String checksumSha256,
            MediaAssetStatusEnum status,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = id;
        this.ownerAccountId = ownerAccountId;
        this.assetKind = assetKind;
        this.contentType = contentType;
        this.originalFileName = originalFileName;
        this.objectKey = objectKey;
        this.byteSize = byteSize;
        this.checksumSha256 = checksumSha256;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public UUID getId() { return id; }
    public UUID getOwnerAccountId() { return ownerAccountId; }
    public MediaAssetKindEnum getAssetKind() { return assetKind; }
    public String getContentType() { return contentType; }
    public String getOriginalFileName() { return originalFileName; }
    public String getObjectKey() { return objectKey; }
    public Long getByteSize() { return byteSize; }
    public String getChecksumSha256() { return checksumSha256; }
    public MediaAssetStatusEnum getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
