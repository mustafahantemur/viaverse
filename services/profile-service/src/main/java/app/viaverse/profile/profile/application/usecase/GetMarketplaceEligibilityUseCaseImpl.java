package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.GetMarketplaceEligibilityUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.IndividualProviderProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.web.logging.ObservedAction;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetMarketplaceEligibilityUseCaseImpl implements GetMarketplaceEligibilityUseCase {

    private final ProfileCapabilityRepository capabilityRepository;
    private final ProfileRepository profileRepository;
    private final IndividualProviderProfileRepository individualProviderProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    public GetMarketplaceEligibilityUseCaseImpl(
            ProfileCapabilityRepository capabilityRepository,
            ProfileRepository profileRepository,
            IndividualProviderProfileRepository individualProviderProfileRepository,
            BusinessProfileRepository businessProfileRepository
    ) {
        this.capabilityRepository = capabilityRepository;
        this.profileRepository = profileRepository;
        this.individualProviderProfileRepository = individualProviderProfileRepository;
        this.businessProfileRepository = businessProfileRepository;
    }

    @Override
    @ObservedAction("profile.marketplace_eligibility")
    public Result execute(UUID accountId) {
        boolean individualProviderEnabled = capabilityRepository.findByAccountIdAndCapability(
                        accountId,
                        ProfileCapabilityEnum.INDIVIDUAL_PROVIDER
                )
                .map(capability -> capability.isEnabled())
                .orElse(false);
        boolean businessEnabled = capabilityRepository.findByAccountIdAndCapability(
                        accountId,
                        ProfileCapabilityEnum.BUSINESS
                )
                .map(capability -> capability.isEnabled())
                .orElse(false);
        var profile = profileRepository.findByAccountId(accountId);
        var individualProviderProfile = individualProviderProfileRepository.findByAccountId(accountId);
        var businessProfile = businessProfileRepository.findByAccountId(accountId);
        BusinessVerificationStatusEnum businessVerificationStatus = businessProfile
                .map(current -> current.getVerificationStatus())
                .orElse(null);
        return new Result(
                accountId,
                profile.map(current -> current.getActiveMode()).orElse(ActiveModeEnum.CUSTOMER),
                individualProviderEnabled,
                businessEnabled,
                businessVerificationStatus,
                individualProviderProfile.map(current -> current.getServiceCategories()).orElse(Set.of()),
                businessProfile.map(current -> current.getServiceCategories()).orElse(Set.of()),
                individualProviderProfile.map(current -> current.isAcceptsRemote()).orElse(false),
                businessProfile.map(current -> current.getDistrict()).orElse(null),
                businessProfile.map(current -> current.getCity()).orElse(null)
        );
    }
}
