package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.profile.profile.domain.model.ProfilePreference;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.ProfilePreferenceJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfilePreferenceJpaMapper {

    ProfilePreference toDomain(ProfilePreferenceJpaEntity entity);

    ProfilePreferenceJpaEntity toEntity(ProfilePreference preference);
}
