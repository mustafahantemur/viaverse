package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository;

import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileTrustSnapshotJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileTrustSnapshotJpaRepository extends JpaRepository<ProfileTrustSnapshotJpaEntity, UUID> {
}
