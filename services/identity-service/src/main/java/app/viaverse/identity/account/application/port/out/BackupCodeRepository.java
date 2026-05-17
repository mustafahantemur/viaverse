package app.viaverse.identity.account.application.port.out;

import app.viaverse.identity.account.domain.model.BackupCode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BackupCodeRepository {

    BackupCode save(BackupCode code);

    Optional<BackupCode> findByCodeHash(String codeHash);

    List<BackupCode> findByAccountId(UUID accountId);

    void deleteByAccountId(UUID accountId);
}
