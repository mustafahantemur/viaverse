package app.viaverse.media.asset.application.port.in;

import app.viaverse.media.asset.domain.enums.MediaAssetKindEnum;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.media.asset.domain.model.MediaUploadSession;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

public interface CreateUploadSessionUseCase {
    Result execute(Command command);

    record Command(
            UUID ownerAccountId,
            MediaAssetKindEnum assetKind,
            String contentType,
            String originalFileName
    ) {
    }

    record Result(
            MediaAsset asset,
            MediaUploadSession uploadSession,
            URI uploadUrl,
            Map<String, String> requiredHeaders
    ) {
    }
}
