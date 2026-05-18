package app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.trustgamification.trust.application.port.out.TrustStateRepository;
import app.viaverse.trustgamification.trust.domain.model.TrustState;
import app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.entity.TrustStateJpaEntity;
import app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.repository.TrustStateJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class TrustStateJpaAdapter implements TrustStateRepository {

    private final TrustStateJpaRepository repository;

    public TrustStateJpaAdapter(TrustStateJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TrustState save(TrustState trustState) {
        TrustStateJpaEntity saved = repository.save(toEntity(trustState));
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrustState> findByAccountId(UUID accountId) {
        return repository.findById(accountId).map(this::toDomain);
    }

    private TrustStateJpaEntity toEntity(TrustState state) {
        return new TrustStateJpaEntity(
                state.getAccountId(),
                state.getScore(),
                state.getLevel(),
                state.getBadge(),
                state.getScoreVersion(),
                state.getCreatedAt(),
                state.getUpdatedAt(),
                state.getVersion()
        );
    }

    private TrustState toDomain(TrustStateJpaEntity entity) {
        return new TrustState(
                entity.getAccountId(),
                entity.getScore(),
                entity.getLevel(),
                entity.getBadge(),
                entity.getScoreVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
