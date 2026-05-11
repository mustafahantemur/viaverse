package app.viaverse.identity.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionJpaRepository extends JpaRepository<AuthSessionJpaEntity, UUID> {
    List<AuthSessionJpaEntity> findByAccountId(UUID accountId);
}
