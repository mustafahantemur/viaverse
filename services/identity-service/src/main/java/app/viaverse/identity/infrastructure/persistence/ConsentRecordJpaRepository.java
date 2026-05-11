package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.ConsentType;
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
