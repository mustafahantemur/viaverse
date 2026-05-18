package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository;

import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileJpaRepository extends JpaRepository<ProfileJpaEntity, UUID> {
}
