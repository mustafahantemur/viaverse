package app.viaverse.media.asset.infrastructure.adapter.out.persistence.repository;

import app.viaverse.media.asset.infrastructure.adapter.out.persistence.entity.MediaUploadSessionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaUploadSessionJpaRepository extends JpaRepository<MediaUploadSessionJpaEntity, UUID> {
}
