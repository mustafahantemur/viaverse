package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfileJpaEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ProfileJpaMapper {

    @Mapping(target = "withCompletenessScore", ignore = true)
    Profile toDomain(ProfileJpaEntity entity);

    ProfileJpaEntity toEntity(Profile profile);
}
