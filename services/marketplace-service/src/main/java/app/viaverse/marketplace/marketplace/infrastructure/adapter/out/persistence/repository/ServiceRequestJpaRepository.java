package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.domain.enums.ServiceRequestStatusEnum;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity.ServiceRequestJpaEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestJpaRepository extends JpaRepository<ServiceRequestJpaEntity, UUID> {

    List<ServiceRequestJpaEntity> findAllByStatusOrderByCreatedAtDesc(ServiceRequestStatusEnum status);

    List<ServiceRequestJpaEntity> findAllByStatusAndCategoryInOrderByCreatedAtDesc(
            ServiceRequestStatusEnum status,
            Set<MarketplaceServiceCategory> categories
    );

    List<ServiceRequestJpaEntity> findAllByRequesterAccountIdOrderByCreatedAtDesc(UUID requesterAccountId);
}
