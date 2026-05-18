package app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper;

import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.BusinessProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusinessProfileDtoMapper {

    BusinessProfileResponse toResponse(BusinessProfile profile);
}
