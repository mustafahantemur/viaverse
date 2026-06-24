package app.viaverse.identity.consent.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.consent.infrastructure.adapter.out.persistence.entity.ConsentRecordJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRecordJpaRepository extends JpaRepository<ConsentRecordJpaEntity, UUID> {
    Optional<ConsentRecordJpaEntity> findByAccountIdAndConsentTypeAndVersion(
            UUID accountId,
            ConsentTypeEnum consentType,
            String version
    );
}
