package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.IndividualProviderProfileJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndividualProviderProfileJpaMapper {

    IndividualProviderProfile toDomain(IndividualProviderProfileJpaEntity entity);

    IndividualProviderProfileJpaEntity toEntity(IndividualProviderProfile profile);
}
