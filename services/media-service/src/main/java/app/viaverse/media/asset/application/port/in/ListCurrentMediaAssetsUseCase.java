package app.viaverse.media.asset.application.port.in;

import app.viaverse.media.asset.domain.model.MediaAsset;
import java.util.List;
import java.util.UUID;

public interface ListCurrentMediaAssetsUseCase {
    List<MediaAsset> execute(UUID ownerAccountId);
}
