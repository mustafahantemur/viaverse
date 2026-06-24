package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.DisableIndividualProviderUseCase;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisableIndividualProviderUseCaseImpl implements DisableIndividualProviderUseCase {

    private final ProfileCapabilityRepository capabilityRepository;
    private final ProfileRepository profileRepository;
    private final ProfileEventPublisher eventPublisher;
    private final Clock clock;

    public DisableIndividualProviderUseCaseImpl(
            ProfileCapabilityRepository capabilityRepository,
            ProfileRepository profileRepository,
            ProfileEventPublisher eventPublisher,
            Clock clock
    ) {
        this.capabilityRepository = capabilityRepository;
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.capability.individual-provider.disable")
    @Transactional
    public Result execute(java.util.UUID accountId) {
        Instant now = clock.instant();
        ProfileCapability capability = capabilityRepository.findByAccountIdAndCapability(
                        accountId,
                        ProfileCapabilityEnum.INDIVIDUAL_PROVIDER
                )
                .orElseThrow(() -> new NotFoundException("Individual provider capability not found"));
        ProfileCapability disabled = capability.isEnabled()
                ? capabilityRepository.save(capability.disable(now))
                : capability;
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        Profile nextProfile = profile.getActiveMode() == ActiveModeEnum.INDIVIDUAL_PROVIDER
                ? profileRepository.save(profile.switchActiveMode(ActiveModeEnum.CUSTOMER, now))
                : profile;
        if (capability.isEnabled()) {
            eventPublisher.publishCapabilityDisabled(disabled);
        }
        return new Result(disabled, nextProfile);
    }
}
