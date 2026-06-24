package app.viaverse.media.asset.infrastructure.adapter.in.web.dto.response;

import app.viaverse.media.asset.domain.enums.MediaAssetKindEnum;
import app.viaverse.media.asset.domain.enums.MediaAssetStatusEnum;
import java.time.Instant;
import java.util.UUID;

public record MediaAssetResponse(
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
        Instant updatedAt
) {
}
