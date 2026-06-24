package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.marketplace.marketplace.application.port.out.JobTimelineRepository;
import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.mapper.MarketplaceJpaMapper;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository.JobTimelineEntryJpaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class JobTimelineJpaAdapter implements JobTimelineRepository {

    private final JobTimelineEntryJpaRepository repository;
    private final MarketplaceJpaMapper mapper;

    public JobTimelineJpaAdapter(
            JobTimelineEntryJpaRepository repository,
            MarketplaceJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public JobTimelineEntry save(JobTimelineEntry entry) {
        return mapper.toDomain(repository.save(mapper.toEntity(entry)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobTimelineEntry> findAllByJobId(UUID jobId) {
        return repository.findAllByJobIdOrderByOccurredAtAscCreatedAtAsc(jobId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
