package app.viaverse.identity.auth.infrastructure.persistence.repository;

import app.viaverse.identity.auth.domain.enums.IdentifierType;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthLoginFlowJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthLoginFlowJpaRepository extends JpaRepository<AuthLoginFlowJpaEntity, UUID> {
    Optional<AuthLoginFlowJpaEntity> findByRegistrationTokenHash(String registrationTokenHash);

    Optional<AuthLoginFlowJpaEntity> findTopByIdentifierTypeAndNormalizedIdentifierAndStatusOrderByCreatedAtDesc(
            IdentifierType identifierType,
            String normalizedIdentifier,
            LoginFlowStatus status
    );
}
