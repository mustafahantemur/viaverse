package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.identity.auth.application.port.out.RefreshTokenRepository;
import app.viaverse.identity.auth.domain.enums.RefreshTokenStatusEnum;
import app.viaverse.identity.auth.domain.model.RefreshToken;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.mapper.RefreshTokenJpaMapper;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository.AuthRefreshTokenJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class RefreshTokenJpaAdapter implements RefreshTokenRepository {

    private final AuthRefreshTokenJpaRepository repository;
    private final RefreshTokenJpaMapper mapper;

    public RefreshTokenJpaAdapter(AuthRefreshTokenJpaRepository repository, RefreshTokenJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return mapper.toDomain(repository.save(mapper.toEntity(token)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findActiveBySessionId(UUID sessionId) {
        return repository.findBySessionIdAndStatus(sessionId, RefreshTokenStatusEnum.ACTIVE)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
