package app.viaverse.identity.shared.audit;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
}
