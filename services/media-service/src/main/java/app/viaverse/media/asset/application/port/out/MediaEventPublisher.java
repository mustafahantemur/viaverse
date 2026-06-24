package app.viaverse.media.asset.application.port.out;

import app.viaverse.media.asset.domain.model.MediaAsset;

public interface MediaEventPublisher {
    void publishAssetReady(MediaAsset asset);
}
