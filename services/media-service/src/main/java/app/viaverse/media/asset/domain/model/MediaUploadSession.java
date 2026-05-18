package app.viaverse.media.asset.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class MediaUploadSession {
    private final UUID id;
    private final UUID assetId;
    private final Instant expiresAt;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public MediaUploadSession(
            UUID id,
            UUID assetId,
            Instant expiresAt,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.assetId = Objects.requireNonNull(assetId, "assetId");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static MediaUploadSession create(UUID assetId, Instant expiresAt, Instant now) {
        return new MediaUploadSession(UUID.randomUUID(), assetId, expiresAt, now, now, 0);
    }

    public UUID getId() { return id; }
    public UUID getAssetId() { return assetId; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
