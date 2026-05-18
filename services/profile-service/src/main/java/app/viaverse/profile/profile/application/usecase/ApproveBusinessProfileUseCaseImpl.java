package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.ApproveBusinessProfileUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApproveBusinessProfileUseCaseImpl implements ApproveBusinessProfileUseCase {

    private final BusinessProfileRepository businessProfileRepository;
    private final ProfileCapabilityRepository capabilityRepository;
    private final ProfileRepository profileRepository;
    private final ProfileEventPublisher eventPublisher;
    private final Clock clock;

    public ApproveBusinessProfileUseCaseImpl(
            BusinessProfileRepository businessProfileRepository,
            ProfileCapabilityRepository capabilityRepository,
            ProfileRepository profileRepository,
            ProfileEventPublisher eventPublisher,
            Clock clock
    ) {
        this.businessProfileRepository = businessProfileRepository;
        this.capabilityRepository = capabilityRepository;
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.business.approve")
    @Transactional
    public BusinessProfile execute(UUID accountId) {
        Instant now = clock.instant();
        BusinessProfile approved = businessProfileRepository.save(businessProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Business profile not found"))
                .approve(now));
        ProfileCapability capability = capabilityRepository.findByAccountIdAndCapability(
                        accountId,
                        ProfileCapabilityEnum.BUSINESS
                )
                .orElseThrow(() -> new NotFoundException("Business capability not found"));
        capabilityRepository.save(capability.enable(now));
        profileRepository.findByAccountId(accountId)
                .ifPresent(profile -> profileRepository.save(profile.switchActiveMode(ActiveModeEnum.BUSINESS, now)));
        eventPublisher.publishBusinessApproved(approved);
        return approved;
    }
}
