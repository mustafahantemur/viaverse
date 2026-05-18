package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.marketplace.marketplace.application.port.out.JobRepository;
import app.viaverse.marketplace.marketplace.domain.model.Job;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.mapper.MarketplaceJpaMapper;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository.JobJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class JobJpaAdapter implements JobRepository {

    private final JobJpaRepository repository;
    private final MarketplaceJpaMapper mapper;

    public JobJpaAdapter(
            JobJpaRepository repository,
            MarketplaceJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Job save(Job job) {
        return mapper.toDomain(repository.save(mapper.toEntity(job)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Job> findById(UUID jobId) {
        return repository.findById(jobId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> findAllByParticipantAccountId(UUID accountId) {
        return repository.findAllByRequesterAccountIdOrProviderAccountIdOrderByCreatedAtDesc(accountId, accountId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
