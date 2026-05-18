package app.viaverse.media.asset.infrastructure.adapter.in.web.dto.request;

import app.viaverse.media.asset.domain.enums.MediaAssetKindEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUploadSessionRequest(
        @NotNull MediaAssetKindEnum assetKind,
        @NotBlank @Size(max = 160) String contentType,
        @Size(max = 255) String originalFileName
) {
}
