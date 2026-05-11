package app.viaverse.identity.auth.infrastructure.persistence.repository;

import app.viaverse.identity.auth.domain.enums.RefreshTokenStatus;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthRefreshTokenJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRefreshTokenJpaRepository extends JpaRepository<AuthRefreshTokenJpaEntity, UUID> {
    Optional<AuthRefreshTokenJpaEntity> findByTokenHashAndStatus(String tokenHash, RefreshTokenStatus status);

    Optional<AuthRefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    List<AuthRefreshTokenJpaEntity> findBySessionIdAndStatus(UUID sessionId, RefreshTokenStatus status);
}
