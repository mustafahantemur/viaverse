package app.viaverse.identity.account.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.identity.account.domain.model.BackupCode;
import app.viaverse.identity.account.infrastructure.adapter.out.persistence.entity.AccountBackupCodeJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountBackupCodeJpaMapper {
    BackupCode toDomain(AccountBackupCodeJpaEntity entity);

    AccountBackupCodeJpaEntity toEntity(BackupCode domain);
}
