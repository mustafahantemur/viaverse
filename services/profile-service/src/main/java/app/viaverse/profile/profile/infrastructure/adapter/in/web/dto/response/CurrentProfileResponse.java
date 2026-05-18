package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response;

import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityStatusEnum;
import app.viaverse.profile.profile.domain.enums.ProviderVerificationLevelEnum;
import app.viaverse.profile.profile.domain.enums.PublicVisibilityEnum;
import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CurrentProfileResponse(
        UUID accountId,
        String displayName,
        String firstName,
        String lastName,
        UUID avatarMediaId,
        String headline,
        String bio,
        String locale,
        String timezone,
        ActiveModeEnum activeMode,
        int completenessScore,
        PublicVisibilityEnum publicVisibility,
        List<CapabilityResponse> capabilities,
        IndividualProviderProfileResponse individualProviderProfile,
        BusinessProfileResponse businessProfile
) {
    public record CapabilityResponse(
            ProfileCapabilityEnum capability,
            ProfileCapabilityStatusEnum status,
            ProviderVerificationLevelEnum verificationLevel,
            Instant enabledAt,
            Instant disabledAt
    ) {
    }

    public record IndividualProviderProfileResponse(
            String serviceBlurb,
            String availabilitySummary,
            boolean acceptsRemote,
            String providerTermsVersionAccepted
    ) {
    }

    public record BusinessProfileResponse(
            String legalName,
            String tradeName,
            BusinessSectorEnum sector,
            String taxId,
            String addressLine,
            String district,
            String city,
            String country,
            String phone,
            String emailPublic,
            UUID logoMediaId,
            String openingHoursJson,
            BusinessVerificationStatusEnum verificationStatus,
            String businessTermsVersionAccepted,
            String rejectionReason
    ) {
    }
}
