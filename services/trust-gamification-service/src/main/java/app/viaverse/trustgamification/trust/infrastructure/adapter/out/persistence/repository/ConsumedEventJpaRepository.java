package app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.repository;

import app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.entity.ConsumedEventJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumedEventJpaRepository extends JpaRepository<ConsumedEventJpaEntity, UUID> {
}
