package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.BusinessProfileJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessProfileJpaMapper {

    @Mapping(target = "approve", ignore = true)
    BusinessProfile toDomain(BusinessProfileJpaEntity entity);

    BusinessProfileJpaEntity toEntity(BusinessProfile profile);
}
