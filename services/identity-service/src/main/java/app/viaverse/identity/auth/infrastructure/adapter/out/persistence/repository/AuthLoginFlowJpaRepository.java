package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.AuthLoginFlowJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthLoginFlowJpaRepository extends JpaRepository<AuthLoginFlowJpaEntity, UUID> {
    Optional<AuthLoginFlowJpaEntity> findByRegistrationTokenHash(String registrationTokenHash);

    Optional<AuthLoginFlowJpaEntity> findTopByIdentifierTypeAndNormalizedIdentifierAndStatusOrderByCreatedAtDesc(
            IdentifierTypeEnum identifierType,
            String normalizedIdentifier,
            LoginFlowStatusEnum status
    );
}
