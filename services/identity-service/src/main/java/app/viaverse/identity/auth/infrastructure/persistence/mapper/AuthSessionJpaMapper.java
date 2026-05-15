package app.viaverse.identity.auth.infrastructure.persistence.mapper;

import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthSessionJpaMapper {

    AuthSession toDomain(AuthSessionJpaEntity entity);

    AuthSessionJpaEntity toEntity(AuthSession domain);
}
