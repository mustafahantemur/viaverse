package app.viaverse.marketplace.marketplace.application.port.out;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.domain.enums.ServiceRequestStatusEnum;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ServiceRequestRepository {

    ServiceRequest save(ServiceRequest request);

    Optional<ServiceRequest> findById(UUID requestId);

    List<ServiceRequest> findAllByStatus(ServiceRequestStatusEnum status);

    List<ServiceRequest> findAllOpenByCategories(Set<MarketplaceServiceCategory> categories);

    List<ServiceRequest> findAllByRequesterAccountId(UUID requesterAccountId);
}
