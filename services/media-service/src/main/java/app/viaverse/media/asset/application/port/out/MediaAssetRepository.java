package app.viaverse.media.asset.application.port.out;

import app.viaverse.media.asset.domain.model.MediaAsset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaAssetRepository {
    MediaAsset save(MediaAsset asset);
    Optional<MediaAsset> findById(UUID assetId);
    List<MediaAsset> findAllByOwnerAccountId(UUID ownerAccountId);
}
