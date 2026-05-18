package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.UpdateCurrentActiveModeUseCase;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.shared.kernel.error.ValidationException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCurrentActiveModeUseCaseImpl implements UpdateCurrentActiveModeUseCase {

    private final ProfileRepository profileRepository;
    private final ProfileCapabilityRepository capabilityRepository;
    private final Clock clock;

    public UpdateCurrentActiveModeUseCaseImpl(
            ProfileRepository profileRepository,
            ProfileCapabilityRepository capabilityRepository,
            Clock clock
    ) {
        this.profileRepository = profileRepository;
        this.capabilityRepository = capabilityRepository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.active-mode.update")
    @Transactional
    public Profile execute(Command command) {
        Profile profile = profileRepository.findByAccountId(command.accountId())
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        ProfileCapabilityEnum capability = ProfileCapabilityEnum.valueOf(command.activeMode().name());
        ProfileCapability requestedCapability = capabilityRepository.findByAccountIdAndCapability(
                        command.accountId(),
                        capability
                )
                .orElseThrow(() -> new ValidationException(
                        "Active mode must refer to an enabled capability",
                        Map.of("activeMode", "capability is not enabled")
                ));
        if (!requestedCapability.isEnabled()) {
            throw new ValidationException(
                    "Active mode must refer to an enabled capability",
                    Map.of("activeMode", "capability is not enabled")
            );
        }
        return profileRepository.save(profile.switchActiveMode(command.activeMode(), clock.instant()));
    }
}
