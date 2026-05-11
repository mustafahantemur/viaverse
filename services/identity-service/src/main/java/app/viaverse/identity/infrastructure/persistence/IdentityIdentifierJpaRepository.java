package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.IdentifierType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityIdentifierJpaRepository extends JpaRepository<IdentityIdentifierJpaEntity, UUID> {
    Optional<IdentityIdentifierJpaEntity> findByIdentifierTypeAndNormalizedIdentifier(
            IdentifierType identifierType,
            String normalizedIdentifier
    );
}
