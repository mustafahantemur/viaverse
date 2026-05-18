package app.viaverse.content.post.infrastructure.adapter.out.persistence.repository;

import app.viaverse.content.post.domain.enums.ContentSignalTypeEnum;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.entity.ContentInteractionJpaEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentInteractionJpaRepository extends JpaRepository<ContentInteractionJpaEntity, UUID> {
    @Query("""
            select distinct interaction.postId
            from ContentInteractionJpaEntity interaction
            where interaction.viewerAccountId = :viewerAccountId
              and interaction.signalType in :signalTypes
            """)
    Set<UUID> findSuppressedPostIds(
            @Param("viewerAccountId") UUID viewerAccountId,
            @Param("signalTypes") List<ContentSignalTypeEnum> signalTypes
    );
}
