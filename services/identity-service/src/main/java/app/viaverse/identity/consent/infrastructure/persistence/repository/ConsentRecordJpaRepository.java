package app.viaverse.identity.consent.infrastructure.persistence.repository;

import app.viaverse.identity.consent.domain.ConsentType;
import app.viaverse.identity.consent.infrastructure.persistence.entity.ConsentRecordJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRecordJpaRepository extends JpaRepository<ConsentRecordJpaEntity, UUID> {
    Optional<ConsentRecordJpaEntity> findByAccountIdAndConsentTypeAndVersion(
            UUID accountId,
            ConsentType consentType,
            String version
    );
}
