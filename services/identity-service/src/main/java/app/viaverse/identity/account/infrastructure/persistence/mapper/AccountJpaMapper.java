package app.viaverse.identity.account.infrastructure.persistence.mapper;

import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountJpaMapper {

    Account toDomain(IdentityAccountJpaEntity entity);

    IdentityAccountJpaEntity toEntity(Account domain);
}
