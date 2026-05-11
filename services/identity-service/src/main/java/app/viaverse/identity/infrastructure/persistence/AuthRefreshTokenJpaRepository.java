package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.RefreshTokenStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRefreshTokenJpaRepository extends JpaRepository<AuthRefreshTokenJpaEntity, UUID> {
    Optional<AuthRefreshTokenJpaEntity> findByTokenHashAndStatus(String tokenHash, RefreshTokenStatus status);

    Optional<AuthRefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    List<AuthRefreshTokenJpaEntity> findBySessionIdAndStatus(UUID sessionId, RefreshTokenStatus status);
}
