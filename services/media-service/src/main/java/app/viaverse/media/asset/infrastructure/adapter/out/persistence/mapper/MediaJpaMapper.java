package app.viaverse.media.asset.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.media.asset.domain.model.MediaUploadSession;
import app.viaverse.media.asset.infrastructure.adapter.out.persistence.entity.MediaAssetJpaEntity;
import app.viaverse.media.asset.infrastructure.adapter.out.persistence.entity.MediaUploadSessionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MediaJpaMapper {
    public MediaAssetJpaEntity toEntity(MediaAsset asset) {
        return new MediaAssetJpaEntity(
                asset.getId(),
                asset.getOwnerAccountId(),
                asset.getAssetKind(),
                asset.getContentType(),
                asset.getOriginalFileName(),
                asset.getObjectKey(),
                asset.getByteSize(),
                asset.getChecksumSha256(),
                asset.getStatus(),
                asset.getCreatedAt(),
                asset.getUpdatedAt(),
                asset.getVersion()
        );
    }

    public MediaAsset toDomain(MediaAssetJpaEntity entity) {
        return new MediaAsset(
                entity.getId(),
                entity.getOwnerAccountId(),
                entity.getAssetKind(),
                entity.getContentType(),
                entity.getOriginalFileName(),
                entity.getObjectKey(),
                entity.getByteSize(),
                entity.getChecksumSha256(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public MediaUploadSessionJpaEntity toEntity(MediaUploadSession session) {
        return new MediaUploadSessionJpaEntity(
                session.getId(),
                session.getAssetId(),
                session.getExpiresAt(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                session.getVersion()
        );
    }

    public MediaUploadSession toDomain(MediaUploadSessionJpaEntity entity) {
        return new MediaUploadSession(
                entity.getId(),
                entity.getAssetId(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
