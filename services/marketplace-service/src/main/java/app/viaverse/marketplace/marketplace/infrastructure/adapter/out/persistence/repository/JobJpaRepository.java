package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository;

import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.JobJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobJpaRepository extends JpaRepository<JobJpaEntity, UUID> {

    List<JobJpaEntity> findAllByRequesterAccountIdOrProviderAccountIdOrderByCreatedAtDesc(
            UUID requesterAccountId,
            UUID providerAccountId
    );
}
