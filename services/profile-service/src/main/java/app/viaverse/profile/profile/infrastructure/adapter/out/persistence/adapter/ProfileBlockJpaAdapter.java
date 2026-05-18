package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.ProfileBlockRepository;
import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileBlockJpaId;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper.ProfileBlockJpaMapper;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.ProfileBlockJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProfileBlockJpaAdapter implements ProfileBlockRepository {

    private final ProfileBlockJpaRepository repository;
    private final ProfileBlockJpaMapper mapper;

    public ProfileBlockJpaAdapter(ProfileBlockJpaRepository repository, ProfileBlockJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProfileBlock save(ProfileBlock block) {
        return mapper.toDomain(repository.save(mapper.toEntity(block)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileBlock> findByBlockerAccountIdAndBlockedAccountId(
            UUID blockerAccountId,
            UUID blockedAccountId
    ) {
        return repository.findById(new ProfileBlockJpaId(blockerAccountId, blockedAccountId))
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileBlock> findAllByBlockerAccountId(UUID blockerAccountId) {
        return repository.findAllByBlockerAccountId(blockerAccountId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(ProfileBlock block) {
        repository.deleteById(new ProfileBlockJpaId(block.getBlockerAccountId(), block.getBlockedAccountId()));
    }
}
