package app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.repository;

import app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.entity.TrustStateJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrustStateJpaRepository extends JpaRepository<TrustStateJpaEntity, UUID> {
}
