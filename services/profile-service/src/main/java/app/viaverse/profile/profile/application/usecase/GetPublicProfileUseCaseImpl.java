package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.GetPublicProfileUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileBlockRepository;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.enums.PublicVisibilityEnum;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import org.springframework.stereotype.Service;

@Service
public class GetPublicProfileUseCaseImpl implements GetPublicProfileUseCase {

    private final ProfileRepository profileRepository;
    private final ProfileBlockRepository profileBlockRepository;
    private final ProfileCapabilityRepository profileCapabilityRepository;
    private final BusinessProfileRepository businessProfileRepository;

    public GetPublicProfileUseCaseImpl(
            ProfileRepository profileRepository,
            ProfileBlockRepository profileBlockRepository,
            ProfileCapabilityRepository profileCapabilityRepository,
            BusinessProfileRepository businessProfileRepository
    ) {
        this.profileRepository = profileRepository;
        this.profileBlockRepository = profileBlockRepository;
        this.profileCapabilityRepository = profileCapabilityRepository;
        this.businessProfileRepository = businessProfileRepository;
    }

    @Override
    @ObservedAction("profile.public")
    public Result execute(Command command) {
        Profile profile = profileRepository.findByAccountId(command.targetAccountId())
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        boolean selfView = command.viewerAccountId() != null
                && command.viewerAccountId().equals(command.targetAccountId());
        boolean blockedViewer = command.viewerAccountId() != null
                && profileBlockRepository.findByBlockerAccountIdAndBlockedAccountId(
                        command.targetAccountId(),
                        command.viewerAccountId()
                ).isPresent();
        boolean privateView = !selfView
                && (blockedViewer || profile.getPublicVisibility() == PublicVisibilityEnum.PRIVATE);
        boolean limitedAnonymousView = !selfView
                && profile.getPublicVisibility() == PublicVisibilityEnum.LIMITED
                && command.viewerAccountId() == null;
        var publicCapabilities = privateView
                ? java.util.List.<ProfileCapabilityEnum>of()
                : profileCapabilityRepository.findAllByAccountId(profile.getAccountId()).stream()
                        .filter(capability -> capability.isEnabled())
                        .map(capability -> capability.getCapability())
                        .toList();
        BusinessProfile businessProfile = businessProfileRepository.findByAccountId(profile.getAccountId())
                .filter(candidate -> candidate.getVerificationStatus() == BusinessVerificationStatusEnum.APPROVED)
                .orElse(null);
        BusinessPreview businessPreview = privateView || limitedAnonymousView || businessProfile == null
                ? null
                : new BusinessPreview(
                        businessProfile.getTradeName(),
                        businessProfile.getSector(),
                        businessProfile.getCity(),
                        businessProfile.getCountry(),
                        businessProfile.getPhone(),
                        businessProfile.getEmailPublic(),
                        businessProfile.getLogoMediaId(),
                        businessProfile.getOpeningHoursJson()
                );
        return new Result(
                profile.getAccountId(),
                profile.getDisplayName(),
                profile.getAvatarMediaId(),
                privateView || limitedAnonymousView ? null : profile.getHeadline(),
                privateView || limitedAnonymousView ? null : profile.getBio(),
                publicCapabilities,
                businessPreview
        );
    }
}
