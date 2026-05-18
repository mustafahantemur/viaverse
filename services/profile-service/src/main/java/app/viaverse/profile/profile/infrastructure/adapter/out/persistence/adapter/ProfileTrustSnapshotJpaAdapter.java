package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.profile.profile.application.port.out.ProfileTrustSnapshotRepository;
import app.viaverse.profile.profile.domain.model.ProfileTrustSnapshot;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileTrustSnapshotJpaEntity;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository.ProfileTrustSnapshotJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProfileTrustSnapshotJpaAdapter implements ProfileTrustSnapshotRepository {

    private final ProfileTrustSnapshotJpaRepository repository;

    public ProfileTrustSnapshotJpaAdapter(ProfileTrustSnapshotJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProfileTrustSnapshot save(ProfileTrustSnapshot snapshot) {
        ProfileTrustSnapshotJpaEntity saved = repository.save(toEntity(snapshot));
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileTrustSnapshot> findByAccountId(UUID accountId) {
        return repository.findById(accountId).map(this::toDomain);
    }

    private ProfileTrustSnapshotJpaEntity toEntity(ProfileTrustSnapshot snapshot) {
        return new ProfileTrustSnapshotJpaEntity(
                snapshot.getAccountId(),
                snapshot.getScore(),
                snapshot.getLevel(),
                snapshot.getBadge(),
                snapshot.getSourceOccurredAt(),
                snapshot.getCreatedAt(),
                snapshot.getUpdatedAt(),
                snapshot.getVersion()
        );
    }

    private ProfileTrustSnapshot toDomain(ProfileTrustSnapshotJpaEntity entity) {
        return new ProfileTrustSnapshot(
                entity.getAccountId(),
                entity.getScore(),
                entity.getLevel(),
                entity.getBadge(),
                entity.getSourceOccurredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
