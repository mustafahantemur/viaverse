package app.viaverse.identity.auth.infrastructure.persistence.mapper;

import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.infrastructure.persistence.entity.IdentityIdentifierJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdentityIdentifierJpaMapper {

    IdentityIdentifier toDomain(IdentityIdentifierJpaEntity entity);

    IdentityIdentifierJpaEntity toEntity(IdentityIdentifier domain);
}
