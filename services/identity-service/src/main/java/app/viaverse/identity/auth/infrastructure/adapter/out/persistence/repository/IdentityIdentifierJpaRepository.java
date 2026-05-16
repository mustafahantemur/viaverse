package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.auth.domain.enums.IdentifierType;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.IdentityIdentifierJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityIdentifierJpaRepository extends JpaRepository<IdentityIdentifierJpaEntity, UUID> {
    Optional<IdentityIdentifierJpaEntity> findByIdentifierTypeAndNormalizedIdentifier(
            IdentifierType identifierType,
            String normalizedIdentifier
    );
}
