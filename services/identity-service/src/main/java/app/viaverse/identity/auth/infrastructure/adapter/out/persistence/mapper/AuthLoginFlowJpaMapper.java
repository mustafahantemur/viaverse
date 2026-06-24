package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.AuthLoginFlowJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthLoginFlowJpaMapper {

    AuthLoginFlow toDomain(AuthLoginFlowJpaEntity entity);

    AuthLoginFlowJpaEntity toEntity(AuthLoginFlow domain);
}
