package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.IdentifierType;
import app.viaverse.identity.domain.auth.LoginFlowStatus;
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
