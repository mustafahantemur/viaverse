package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.auth.domain.enums.SessionStatusEnum;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionJpaRepository extends JpaRepository<AuthSessionJpaEntity, UUID> {
    List<AuthSessionJpaEntity> findByAccountId(UUID accountId);

    List<AuthSessionJpaEntity> findByAccountIdAndStatus(UUID accountId, SessionStatusEnum status);
}
