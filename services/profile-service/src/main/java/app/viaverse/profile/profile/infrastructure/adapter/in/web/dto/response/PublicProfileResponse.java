package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response;

import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import java.util.List;
import java.util.UUID;

public record PublicProfileResponse(
        UUID accountId,
        String displayName,
        UUID avatarMediaId,
        String headline,
        String bio,
        List<ProfileCapabilityEnum> capabilities,
        BusinessPreviewResponse businessProfile
) {
    public record BusinessPreviewResponse(
            String tradeName,
            BusinessSectorEnum sector,
            String city,
            String country,
            String phone,
            String emailPublic,
            UUID logoMediaId,
            String openingHoursJson
    ) {
    }
}
