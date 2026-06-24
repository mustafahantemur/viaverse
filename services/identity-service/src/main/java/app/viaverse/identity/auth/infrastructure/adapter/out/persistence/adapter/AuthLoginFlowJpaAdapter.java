package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.mapper.AuthLoginFlowJpaMapper;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository.AuthLoginFlowJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AuthLoginFlowJpaAdapter implements AuthLoginFlowRepository {

    private final AuthLoginFlowJpaRepository repository;
    private final AuthLoginFlowJpaMapper mapper;

    public AuthLoginFlowJpaAdapter(AuthLoginFlowJpaRepository repository, AuthLoginFlowJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public AuthLoginFlow save(AuthLoginFlow flow) {
        return mapper.toDomain(repository.save(mapper.toEntity(flow)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthLoginFlow> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthLoginFlow> findByRegistrationTokenHash(String registrationTokenHash) {
        return repository.findByRegistrationTokenHash(registrationTokenHash).map(mapper::toDomain);
    }
}
