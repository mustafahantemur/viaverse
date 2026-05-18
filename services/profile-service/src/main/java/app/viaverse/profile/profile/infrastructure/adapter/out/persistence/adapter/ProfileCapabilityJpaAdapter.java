package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileCapabilityJpaId;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper.ProfileCapabilityJpaMapper;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.ProfileCapabilityJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProfileCapabilityJpaAdapter implements ProfileCapabilityRepository {

    private final ProfileCapabilityJpaRepository repository;
    private final ProfileCapabilityJpaMapper mapper;

    public ProfileCapabilityJpaAdapter(
            ProfileCapabilityJpaRepository repository,
            ProfileCapabilityJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProfileCapability save(ProfileCapability capability) {
        return mapper.toDomain(repository.save(mapper.toEntity(capability)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileCapability> findByAccountIdAndCapability(
            UUID accountId,
            ProfileCapabilityEnum capability
    ) {
        return repository.findById(new ProfileCapabilityJpaId(
                accountId,
                capability
        )).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileCapability> findAllByAccountId(UUID accountId) {
        return repository.findAllByAccountId(accountId).stream().map(mapper::toDomain).toList();
    }
}
