package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.AuthOtpChallengeJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthOtpChallengeJpaRepository extends JpaRepository<AuthOtpChallengeJpaEntity, UUID> {
    Optional<AuthOtpChallengeJpaEntity> findTopByFlowIdOrderByCreatedAtDesc(UUID flowId);
}
