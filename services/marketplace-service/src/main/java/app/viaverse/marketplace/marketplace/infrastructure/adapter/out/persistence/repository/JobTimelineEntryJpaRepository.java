package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository;

import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.JobTimelineEntryJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobTimelineEntryJpaRepository extends JpaRepository<JobTimelineEntryJpaEntity, UUID> {

    List<JobTimelineEntryJpaEntity> findAllByJobIdOrderByOccurredAtAscCreatedAtAsc(UUID jobId);
}
