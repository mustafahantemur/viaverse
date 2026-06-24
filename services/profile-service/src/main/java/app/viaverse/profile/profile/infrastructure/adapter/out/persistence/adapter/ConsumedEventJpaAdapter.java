package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.ConsumedEventRepository;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ConsumedEventJpaEntity;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.ConsumedEventJpaRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ConsumedEventJpaAdapter implements ConsumedEventRepository {

    private final ConsumedEventJpaRepository repository;

    public ConsumedEventJpaAdapter(ConsumedEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEventId(UUID eventId) {
        return repository.existsById(eventId);
    }

    @Override
    public void record(UUID eventId, String eventType, Instant consumedAt) {
        repository.save(new ConsumedEventJpaEntity(eventId, eventType, consumedAt));
    }
}
