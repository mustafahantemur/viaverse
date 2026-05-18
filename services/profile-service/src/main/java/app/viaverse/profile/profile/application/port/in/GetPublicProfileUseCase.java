package app.viaverse.profile.profile.application.port.in;

import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import app.viaverse.profile.profile.domain.enums.TrustBadgeEnum;
import java.util.List;
import java.util.UUID;

public interface GetPublicProfileUseCase {

    Result execute(Command command);

    record Command(UUID targetAccountId, UUID viewerAccountId) {
    }

    record Result(
            UUID accountId,
            String displayName,
            UUID avatarMediaId,
            String headline,
            String bio,
            List<ProfileCapabilityEnum> capabilities,
            BusinessPreview businessProfile,
            TrustBadgeEnum trustBadge
    ) {
    }

    record BusinessPreview(
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
