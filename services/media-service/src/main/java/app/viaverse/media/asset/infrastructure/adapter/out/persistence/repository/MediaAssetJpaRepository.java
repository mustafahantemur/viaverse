package app.viaverse.media.asset.infrastructure.adapter.out.persistence.repository;

import app.viaverse.media.asset.infrastructure.adapter.out.persistence.entity.MediaAssetJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetJpaRepository extends JpaRepository<MediaAssetJpaEntity, UUID> {
    List<MediaAssetJpaEntity> findAllByOwnerAccountIdOrderByCreatedAtDesc(UUID ownerAccountId);
}
