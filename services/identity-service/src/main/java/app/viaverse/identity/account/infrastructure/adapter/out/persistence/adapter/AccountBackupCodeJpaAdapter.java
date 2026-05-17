package app.viaverse.identity.account.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.identity.account.application.port.out.BackupCodeRepository;
import app.viaverse.identity.account.domain.model.BackupCode;
import app.viaverse.identity.account.infrastructure.adapter.out.persistence.mapper.AccountBackupCodeJpaMapper;
import app.viaverse.identity.account.infrastructure.adapter.out.persistence.repository.AccountBackupCodeJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AccountBackupCodeJpaAdapter implements BackupCodeRepository {

    private final AccountBackupCodeJpaRepository repository;
    private final AccountBackupCodeJpaMapper mapper;

    public AccountBackupCodeJpaAdapter(
            AccountBackupCodeJpaRepository repository,
            AccountBackupCodeJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public BackupCode save(BackupCode code) {
        return mapper.toDomain(repository.save(mapper.toEntity(code)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BackupCode> findByCodeHash(String codeHash) {
        return repository.findByCodeHash(codeHash).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BackupCode> findByAccountId(UUID accountId) {
        return repository.findByAccountIdOrderByCreatedAtAsc(accountId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByAccountId(UUID accountId) {
        repository.deleteByAccountId(accountId);
    }
}
