package app.viaverse.identity.consent.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.identity.consent.application.port.out.ConsentRecordRepository;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.consent.infrastructure.adapter.out.persistence.mapper.ConsentRecordJpaMapper;
import app.viaverse.identity.consent.infrastructure.adapter.out.persistence.repository.ConsentRecordJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ConsentRecordJpaAdapter implements ConsentRecordRepository {
    private final ConsentRecordJpaRepository repository;
    private final ConsentRecordJpaMapper mapper;

    public ConsentRecordJpaAdapter(
            ConsentRecordJpaRepository repository,
            ConsentRecordJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(Record record) {
        repository.save(mapper.toEntity(record));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Record> findByAccountIdAndTypeAndVersion(UUID accountId, ConsentTypeEnum type, String version) {
        return repository.findByAccountIdAndConsentTypeAndVersion(accountId, type, version)
                .map(mapper::toRecord);
    }
}
