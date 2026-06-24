package app.viaverse.content.post.infrastructure.adapter.out.persistence.repository;

import app.viaverse.content.post.domain.enums.ContentPostStatusEnum;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.entity.ContentPostJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentPostJpaRepository extends JpaRepository<ContentPostJpaEntity, UUID> {
    List<ContentPostJpaEntity> findAllByAuthorAccountIdOrderByCreatedAtDesc(UUID authorAccountId);
    List<ContentPostJpaEntity> findAllByStatusOrderByPublishedAtDesc(ContentPostStatusEnum status);
    List<ContentPostJpaEntity> findAllByStatusAndCityOrderByPublishedAtDesc(
            ContentPostStatusEnum status,
            String city
    );
    List<ContentPostJpaEntity> findAllByStatusAndCityAndDistrictOrderByPublishedAtDesc(
            ContentPostStatusEnum status,
            String city,
            String district
    );
}
