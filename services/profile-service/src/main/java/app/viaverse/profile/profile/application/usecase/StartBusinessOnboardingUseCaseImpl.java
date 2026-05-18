package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.StartBusinessOnboardingUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityStatusEnum;
import app.viaverse.profile.profile.domain.enums.ProviderVerificationLevelEnum;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartBusinessOnboardingUseCaseImpl implements StartBusinessOnboardingUseCase {

    private final ProfileCapabilityRepository capabilityRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final Clock clock;

    public StartBusinessOnboardingUseCaseImpl(
            ProfileCapabilityRepository capabilityRepository,
            BusinessProfileRepository businessProfileRepository,
            Clock clock
    ) {
        this.capabilityRepository = capabilityRepository;
        this.businessProfileRepository = businessProfileRepository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.business.start")
    @Transactional
    public Result execute(java.util.UUID accountId) {
        Instant now = clock.instant();
        ProfileCapability capability = capabilityRepository.findByAccountIdAndCapability(
                        accountId,
                        ProfileCapabilityEnum.BUSINESS
                )
                .orElseGet(() -> capabilityRepository.save(new ProfileCapability(
                        accountId,
                        ProfileCapabilityEnum.BUSINESS,
                        ProfileCapabilityStatusEnum.PENDING_REVIEW,
                        now,
                        null,
                        ProviderVerificationLevelEnum.NONE,
                        now,
                        now,
                        0
                )));
        BusinessProfile businessProfile = businessProfileRepository.findByAccountId(accountId)
                .orElseGet(() -> businessProfileRepository.save(BusinessProfile.start(accountId, now)));
        return new Result(capability, businessProfile);
    }
}
