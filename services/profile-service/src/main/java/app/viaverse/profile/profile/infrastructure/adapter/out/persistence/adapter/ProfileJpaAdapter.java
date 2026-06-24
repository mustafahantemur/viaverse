package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper.ProfileJpaMapper;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.ProfileJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProfileJpaAdapter implements ProfileRepository {

    private final ProfileJpaRepository repository;
    private final ProfileJpaMapper mapper;

    public ProfileJpaAdapter(ProfileJpaRepository repository, ProfileJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Profile save(Profile profile) {
        return mapper.toDomain(repository.save(mapper.toEntity(profile)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findByAccountId(UUID accountId) {
        return repository.findById(accountId).map(mapper::toDomain);
    }
}
