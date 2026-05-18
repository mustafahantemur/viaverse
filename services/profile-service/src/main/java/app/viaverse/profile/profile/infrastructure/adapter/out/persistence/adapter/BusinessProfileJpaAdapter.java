package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper.BusinessProfileJpaMapper;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.BusinessProfileJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class BusinessProfileJpaAdapter implements BusinessProfileRepository {

    private final BusinessProfileJpaRepository repository;
    private final BusinessProfileJpaMapper mapper;

    public BusinessProfileJpaAdapter(
            BusinessProfileJpaRepository repository,
            BusinessProfileJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public BusinessProfile save(BusinessProfile profile) {
        return mapper.toDomain(repository.save(mapper.toEntity(profile)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BusinessProfile> findByAccountId(UUID accountId) {
        return repository.findById(accountId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessProfile> findAllByVerificationStatus(BusinessVerificationStatusEnum status) {
        return repository.findAllByVerificationStatusOrderByUpdatedAtAsc(status).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
