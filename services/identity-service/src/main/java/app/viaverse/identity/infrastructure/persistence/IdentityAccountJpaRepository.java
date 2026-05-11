package app.viaverse.identity.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityAccountJpaRepository extends JpaRepository<IdentityAccountJpaEntity, UUID> {
}
