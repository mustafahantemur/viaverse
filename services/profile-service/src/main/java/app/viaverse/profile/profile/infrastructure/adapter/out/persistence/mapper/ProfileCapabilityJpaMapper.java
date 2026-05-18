package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileCapabilityJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileCapabilityJpaMapper {

    @Mapping(target = "enable", ignore = true)
    @Mapping(target = "disable", ignore = true)
    ProfileCapability toDomain(ProfileCapabilityJpaEntity entity);

    ProfileCapabilityJpaEntity toEntity(ProfileCapability capability);
}
