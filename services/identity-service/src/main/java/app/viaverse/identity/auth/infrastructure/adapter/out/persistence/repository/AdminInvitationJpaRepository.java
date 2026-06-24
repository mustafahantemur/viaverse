package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.AdminInvitationJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminInvitationJpaRepository extends JpaRepository<AdminInvitationJpaEntity, UUID> {
    Optional<AdminInvitationJpaEntity> findByTokenHash(String tokenHash);
}
