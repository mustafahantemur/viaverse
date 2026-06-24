package app.viaverse.media.asset.application.port.in;

import app.viaverse.media.asset.domain.model.MediaAsset;
import java.util.UUID;

public interface CompleteUploadUseCase {
    MediaAsset execute(Command command);

    record Command(
            UUID assetId,
            UUID ownerAccountId,
            String checksumSha256
    ) {
    }
}
