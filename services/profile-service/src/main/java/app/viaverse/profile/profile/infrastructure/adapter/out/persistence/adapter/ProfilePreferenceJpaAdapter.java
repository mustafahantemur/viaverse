package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.ProfilePreferenceRepository;
import app.viaverse.profile.profile.domain.model.ProfilePreference;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfilePreferenceJpaId;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper.ProfilePreferenceJpaMapper;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.ProfilePreferenceJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProfilePreferenceJpaAdapter implements ProfilePreferenceRepository {

    private final ProfilePreferenceJpaRepository repository;
    private final ProfilePreferenceJpaMapper mapper;

    public ProfilePreferenceJpaAdapter(
            ProfilePreferenceJpaRepository repository,
            ProfilePreferenceJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProfilePreference save(ProfilePreference preference) {
        return mapper.toDomain(repository.save(mapper.toEntity(preference)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfilePreference> findByAccountIdAndKey(UUID accountId, String key) {
        return repository.findById(new ProfilePreferenceJpaId(accountId, key)).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfilePreference> findAllByAccountId(UUID accountId) {
        return repository.findAllByAccountId(accountId).stream().map(mapper::toDomain).toList();
    }
}
