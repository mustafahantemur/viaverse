package app.viaverse.identity.account.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.account.infrastructure.adapter.out.persistence.entity.IdentityAccountJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityAccountJpaRepository extends JpaRepository<IdentityAccountJpaEntity, UUID> {
}
