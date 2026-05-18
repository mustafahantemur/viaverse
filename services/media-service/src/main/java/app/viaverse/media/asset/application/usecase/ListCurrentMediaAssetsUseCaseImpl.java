package app.viaverse.media.asset.application.usecase;

import app.viaverse.media.asset.application.port.in.ListCurrentMediaAssetsUseCase;
import app.viaverse.media.asset.application.port.out.MediaAssetRepository;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListCurrentMediaAssetsUseCaseImpl implements ListCurrentMediaAssetsUseCase {
    private final MediaAssetRepository repository;

    public ListCurrentMediaAssetsUseCaseImpl(MediaAssetRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("media.asset.list_current")
    public List<MediaAsset> execute(UUID ownerAccountId) {
        return repository.findAllByOwnerAccountId(ownerAccountId);
    }
}
