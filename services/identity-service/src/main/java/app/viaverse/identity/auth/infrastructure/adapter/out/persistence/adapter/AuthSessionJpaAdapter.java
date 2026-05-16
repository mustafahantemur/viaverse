package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.domain.enums.SessionStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.mapper.AuthSessionJpaMapper;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository.AuthSessionJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AuthSessionJpaAdapter implements AuthSessionRepository {

    private final AuthSessionJpaRepository repository;
    private final AuthSessionJpaMapper mapper;

    public AuthSessionJpaAdapter(AuthSessionJpaRepository repository, AuthSessionJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public AuthSession save(AuthSession session) {
        return mapper.toDomain(repository.save(mapper.toEntity(session)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthSession> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthSession> findActiveByAccountId(UUID accountId) {
        return repository.findByAccountIdAndStatus(accountId, SessionStatusEnum.ACTIVE)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
