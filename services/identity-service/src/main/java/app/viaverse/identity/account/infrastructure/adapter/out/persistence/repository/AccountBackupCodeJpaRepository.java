package app.viaverse.identity.account.infrastructure.adapter.out.persistence.repository;

import app.viaverse.identity.account.infrastructure.adapter.out.persistence.entity.AccountBackupCodeJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBackupCodeJpaRepository extends JpaRepository<AccountBackupCodeJpaEntity, UUID> {

    Optional<AccountBackupCodeJpaEntity> findByCodeHash(String codeHash);

    List<AccountBackupCodeJpaEntity> findByAccountIdOrderByCreatedAtAsc(UUID accountId);

    void deleteByAccountId(UUID accountId);
}
