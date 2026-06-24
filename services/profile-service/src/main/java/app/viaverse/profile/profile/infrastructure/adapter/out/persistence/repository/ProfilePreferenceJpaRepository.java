package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository;

import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfilePreferenceJpaEntity;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfilePreferenceJpaId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePreferenceJpaRepository
        extends JpaRepository<ProfilePreferenceJpaEntity, ProfilePreferenceJpaId> {

    List<ProfilePreferenceJpaEntity> findAllByAccountId(UUID accountId);
}
