package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.domain.enums.IdentifierType;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.mapper.IdentityIdentifierJpaMapper;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository.IdentityIdentifierJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class IdentityIdentifierJpaAdapter implements IdentifierRepository {

    private final IdentityIdentifierJpaRepository repository;
    private final IdentityIdentifierJpaMapper mapper;

    public IdentityIdentifierJpaAdapter(IdentityIdentifierJpaRepository repository, IdentityIdentifierJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public IdentityIdentifier save(IdentityIdentifier identifier) {
        return mapper.toDomain(repository.save(mapper.toEntity(identifier)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdentityIdentifier> findByTypeAndValue(IdentifierType type, String normalizedValue) {
        return repository.findByIdentifierTypeAndNormalizedIdentifier(type, normalizedValue).map(mapper::toDomain);
    }
}
