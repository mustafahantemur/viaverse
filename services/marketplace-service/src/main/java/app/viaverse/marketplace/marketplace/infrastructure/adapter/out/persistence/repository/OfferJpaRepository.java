package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository;

import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.OfferJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferJpaRepository extends JpaRepository<OfferJpaEntity, UUID> {

    Optional<OfferJpaEntity> findByRequestIdAndProviderAccountId(UUID requestId, UUID providerAccountId);

    List<OfferJpaEntity> findAllByRequestIdOrderByCreatedAtDesc(UUID requestId);
}
