package app.viaverse.identity.account.infrastructure.persistence.repository;

import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityAccountJpaRepository extends JpaRepository<IdentityAccountJpaEntity, UUID> {
}
