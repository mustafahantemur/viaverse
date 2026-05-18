package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.application.port.out.ServiceRequestRepository;
import app.viaverse.marketplace.marketplace.domain.enums.ServiceRequestStatusEnum;
import app.viaverse.marketplace.marketplace.domain.model.ServiceRequest;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.mapper.MarketplaceJpaMapper;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository.ServiceRequestJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ServiceRequestJpaAdapter implements ServiceRequestRepository {

    private final ServiceRequestJpaRepository repository;
    private final MarketplaceJpaMapper mapper;

    public ServiceRequestJpaAdapter(
            ServiceRequestJpaRepository repository,
            MarketplaceJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ServiceRequest save(ServiceRequest request) {
        return mapper.toDomain(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceRequest> findById(UUID requestId) {
        return repository.findById(requestId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> findAllByStatus(ServiceRequestStatusEnum status) {
        return repository.findAllByStatusOrderByCreatedAtDesc(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> findAllOpenByCategories(Set<MarketplaceServiceCategory> categories) {
        return repository.findAllByStatusAndCategoryInOrderByCreatedAtDesc(
                        ServiceRequestStatusEnum.OPEN,
                        categories
                ).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> findAllByRequesterAccountId(UUID requesterAccountId) {
        return repository.findAllByRequesterAccountIdOrderByCreatedAtDesc(requesterAccountId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
