package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileBlockJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileBlockJpaMapper {

    ProfileBlock toDomain(ProfileBlockJpaEntity entity);

    ProfileBlockJpaEntity toEntity(ProfileBlock block);
}
