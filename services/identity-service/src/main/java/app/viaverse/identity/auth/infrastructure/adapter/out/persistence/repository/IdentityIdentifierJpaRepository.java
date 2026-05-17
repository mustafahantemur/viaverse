package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.IdentityIdentifierJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityIdentifierJpaRepository extends JpaRepository<IdentityIdentifierJpaEntity, UUID> {
    Optional<IdentityIdentifierJpaEntity> findByIdentifierTypeAndNormalizedIdentifier(
            IdentifierTypeEnum identifierType,
            String normalizedIdentifier
    );

    List<IdentityIdentifierJpaEntity> findByAccountIdOrderByCreatedAtAsc(UUID accountId);
}
