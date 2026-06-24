package app.viaverse.media.asset.infrastructure.adapter.in.web.mapper;

import app.viaverse.media.asset.application.port.in.CreateUploadSessionUseCase;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.media.asset.infrastructure.adapter.in.web.dto.response.MediaAssetResponse;
import app.viaverse.media.asset.infrastructure.adapter.in.web.dto.response.UploadSessionResponse;
import org.springframework.stereotype.Component;

@Component
public class MediaDtoMapper {
    public MediaAssetResponse toResponse(MediaAsset asset) {
        return new MediaAssetResponse(
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
                asset.getUpdatedAt()
        );
    }

    public UploadSessionResponse toResponse(CreateUploadSessionUseCase.Result result) {
        return new UploadSessionResponse(
                result.asset().getId(),
                result.uploadSession().getId(),
                result.uploadUrl(),
                result.requiredHeaders(),
                result.uploadSession().getExpiresAt()
        );
    }
}
