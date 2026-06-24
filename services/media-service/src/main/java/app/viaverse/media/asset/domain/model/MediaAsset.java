package app.viaverse.media.asset.domain.model;

import app.viaverse.media.asset.domain.enums.MediaAssetKindEnum;
import app.viaverse.media.asset.domain.enums.MediaAssetStatusEnum;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class MediaAsset {
    private final UUID id;
    private final UUID ownerAccountId;
    private final MediaAssetKindEnum assetKind;
    private final String contentType;
    private final String originalFileName;
    private final String objectKey;
    private final Long byteSize;
    private final String checksumSha256;
    private final MediaAssetStatusEnum status;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public MediaAsset(
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
        this.id = Objects.requireNonNull(id, "id");
        this.ownerAccountId = Objects.requireNonNull(ownerAccountId, "ownerAccountId");
        this.assetKind = Objects.requireNonNull(assetKind, "assetKind");
        this.contentType = requireText(contentType, "contentType", 160);
        this.originalFileName = optionalText(originalFileName, "originalFileName", 255);
        this.objectKey = requireText(objectKey, "objectKey", 255);
        this.byteSize = optionalSize(byteSize);
        this.checksumSha256 = optionalText(checksumSha256, "checksumSha256", 128);
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static MediaAsset initiate(
            UUID ownerAccountId,
            MediaAssetKindEnum assetKind,
            String contentType,
            String originalFileName,
            Instant now
    ) {
        UUID assetId = UUID.randomUUID();
        String extension = safeExtension(originalFileName);
        return new MediaAsset(
                assetId,
                ownerAccountId,
                assetKind,
                contentType,
                originalFileName,
                ownerAccountId + "/" + assetId + extension,
                null,
                null,
                MediaAssetStatusEnum.INITIATED,
                now,
                now,
                0
        );
    }

    public MediaAsset markReady(long byteSize, String checksumSha256, Instant now) {
        if (status != MediaAssetStatusEnum.INITIATED) {
            throw new IllegalStateException("Only initiated assets can become ready");
        }
        return new MediaAsset(
                id,
                ownerAccountId,
                assetKind,
                contentType,
                originalFileName,
                objectKey,
                byteSize,
                checksumSha256,
                MediaAssetStatusEnum.READY,
                createdAt,
                now,
                version
        );
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

    private static String safeExtension(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank() || !originalFileName.contains(".")) {
            return "";
        }
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        return extension.matches("^\\.[a-z0-9]{1,10}$") ? extension : "";
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

    private static Long optionalSize(Long value) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException("byteSize must be non-negative");
        }
        return value;
    }
}
