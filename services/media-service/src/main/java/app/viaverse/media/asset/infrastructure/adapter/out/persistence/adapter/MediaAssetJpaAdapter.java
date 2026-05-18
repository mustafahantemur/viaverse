package app.viaverse.media.asset.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.media.asset.application.port.out.MediaAssetRepository;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.media.asset.infrastructure.adapter.out.persistence.mapper.MediaJpaMapper;
import app.viaverse.media.asset.infrastructure.adapter.out.persistence.repository.MediaAssetJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class MediaAssetJpaAdapter implements MediaAssetRepository {
    private final MediaAssetJpaRepository repository;
    private final MediaJpaMapper mapper;

    public MediaAssetJpaAdapter(MediaAssetJpaRepository repository, MediaJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MediaAsset save(MediaAsset asset) {
        return mapper.toDomain(repository.save(mapper.toEntity(asset)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MediaAsset> findById(UUID assetId) {
        return repository.findById(assetId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaAsset> findAllByOwnerAccountId(UUID ownerAccountId) {
        return repository.findAllByOwnerAccountIdOrderByCreatedAtDesc(ownerAccountId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
