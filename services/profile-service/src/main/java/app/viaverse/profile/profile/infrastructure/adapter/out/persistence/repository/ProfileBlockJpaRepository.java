package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository;

import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileBlockJpaEntity;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileBlockJpaId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileBlockJpaRepository extends JpaRepository<ProfileBlockJpaEntity, ProfileBlockJpaId> {

    List<ProfileBlockJpaEntity> findAllByBlockerAccountId(UUID blockerAccountId);
}
