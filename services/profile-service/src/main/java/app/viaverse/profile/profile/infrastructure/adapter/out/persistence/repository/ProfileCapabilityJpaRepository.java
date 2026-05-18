package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository;

import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileCapabilityJpaEntity;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileCapabilityJpaId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileCapabilityJpaRepository
        extends JpaRepository<ProfileCapabilityJpaEntity, ProfileCapabilityJpaId> {

    List<ProfileCapabilityJpaEntity> findAllByAccountId(UUID accountId);
}
