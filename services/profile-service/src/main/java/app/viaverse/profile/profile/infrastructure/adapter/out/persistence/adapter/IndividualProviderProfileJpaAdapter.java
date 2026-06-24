package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.IndividualProviderProfileRepository;
import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper.IndividualProviderProfileJpaMapper;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.IndividualProviderProfileJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class IndividualProviderProfileJpaAdapter implements IndividualProviderProfileRepository {

    private final IndividualProviderProfileJpaRepository repository;
    private final IndividualProviderProfileJpaMapper mapper;

    public IndividualProviderProfileJpaAdapter(
            IndividualProviderProfileJpaRepository repository,
            IndividualProviderProfileJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public IndividualProviderProfile save(IndividualProviderProfile profile) {
        return mapper.toDomain(repository.save(mapper.toEntity(profile)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IndividualProviderProfile> findByAccountId(UUID accountId) {
        return repository.findById(accountId).map(mapper::toDomain);
    }
}
