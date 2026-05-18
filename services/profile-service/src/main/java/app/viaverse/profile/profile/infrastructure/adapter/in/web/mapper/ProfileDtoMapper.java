package app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper;

import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.CurrentProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.ProfileBlockResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.PublicProfileResponse;
import app.viaverse.profile.profile.application.port.in.GetPublicProfileUseCase;
import app.viaverse.profile.profile.application.port.in.GetCurrentProfileUseCase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileDtoMapper {

    default CurrentProfileResponse toCurrentProfileResponse(GetCurrentProfileUseCase.Result result) {
        Profile profile = result.profile();
        return new CurrentProfileResponse(
                profile.getAccountId(),
                profile.getDisplayName(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getAvatarMediaId(),
                profile.getHeadline(),
                profile.getBio(),
                profile.getLocale(),
                profile.getTimezone(),
                profile.getActiveMode(),
                profile.getCompletenessScore(),
                profile.getPublicVisibility(),
                result.trustSnapshot()
                        .map(snapshot -> new CurrentProfileResponse.TrustSummaryResponse(
                                snapshot.getScore(),
                                snapshot.getLevel(),
                                snapshot.getBadge(),
                                snapshot.getSourceOccurredAt()
                        ))
                        .orElse(new CurrentProfileResponse.TrustSummaryResponse(
                                0,
                                app.viaverse.profile.profile.domain.enums.TrustLevelEnum.NONE,
                                app.viaverse.profile.profile.domain.enums.TrustBadgeEnum.NONE,
                                null
                        )),
                result.capabilities().stream()
                        .map(capability -> new CurrentProfileResponse.CapabilityResponse(
                                capability.getCapability(),
                                capability.getStatus(),
                                capability.getVerificationLevel(),
                                capability.getEnabledAt(),
                                capability.getDisabledAt()
                        ))
                        .toList(),
                result.individualProviderProfile()
                        .map(providerProfile -> new CurrentProfileResponse.IndividualProviderProfileResponse(
                                providerProfile.getServiceBlurb(),
                                providerProfile.getAvailabilitySummary(),
                                providerProfile.isAcceptsRemote(),
                                providerProfile.getProviderTermsVersionAccepted()
                        ))
                        .orElse(null),
                result.businessProfile()
                        .map(businessProfile -> new CurrentProfileResponse.BusinessProfileResponse(
                                businessProfile.getLegalName(),
                                businessProfile.getTradeName(),
                                businessProfile.getSector(),
                                businessProfile.getTaxId(),
                                businessProfile.getAddressLine(),
                                businessProfile.getDistrict(),
                                businessProfile.getCity(),
                                businessProfile.getCountry(),
                                businessProfile.getPhone(),
                                businessProfile.getEmailPublic(),
                                businessProfile.getLogoMediaId(),
                                businessProfile.getOpeningHoursJson(),
                                businessProfile.getVerificationStatus(),
                                businessProfile.getBusinessTermsVersionAccepted(),
                                businessProfile.getRejectionReason()
                        ))
                        .orElse(null)
        );
    }

    PublicProfileResponse toPublicProfileResponse(GetPublicProfileUseCase.Result result);

    ProfileBlockResponse toProfileBlockResponse(ProfileBlock block);
}
