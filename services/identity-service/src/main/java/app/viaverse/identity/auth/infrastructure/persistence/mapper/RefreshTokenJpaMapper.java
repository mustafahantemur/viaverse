package app.viaverse.identity.auth.infrastructure.persistence.mapper;

import app.viaverse.identity.auth.domain.model.RefreshToken;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthRefreshTokenJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenJpaMapper {

    RefreshToken toDomain(AuthRefreshTokenJpaEntity entity);

    AuthRefreshTokenJpaEntity toEntity(RefreshToken domain);
}
