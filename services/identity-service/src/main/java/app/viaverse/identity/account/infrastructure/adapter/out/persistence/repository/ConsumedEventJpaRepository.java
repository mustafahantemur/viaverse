package app.viaverse.identity.account.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.account.infrastructure.adapter.out.persistence.entity.ConsumedEventJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumedEventJpaRepository extends JpaRepository<ConsumedEventJpaEntity, UUID> {
}
