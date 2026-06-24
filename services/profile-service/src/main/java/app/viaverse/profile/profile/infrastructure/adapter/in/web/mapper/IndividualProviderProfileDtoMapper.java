package app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper;

import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.IndividualProviderProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndividualProviderProfileDtoMapper {

    IndividualProviderProfileResponse toResponse(IndividualProviderProfile profile);
}
