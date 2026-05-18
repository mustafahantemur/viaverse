package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.GetCurrentProfileUseCase;
import app.viaverse.profile.profile.application.port.out.IndividualProviderProfileRepository;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentProfileUseCaseImpl implements GetCurrentProfileUseCase {

    private final ProfileRepository profileRepository;
    private final ProfileCapabilityRepository profileCapabilityRepository;
    private final IndividualProviderProfileRepository individualProviderProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    public GetCurrentProfileUseCaseImpl(
            ProfileRepository profileRepository,
            ProfileCapabilityRepository profileCapabilityRepository,
            IndividualProviderProfileRepository individualProviderProfileRepository,
            BusinessProfileRepository businessProfileRepository
    ) {
        this.profileRepository = profileRepository;
        this.profileCapabilityRepository = profileCapabilityRepository;
        this.individualProviderProfileRepository = individualProviderProfileRepository;
        this.businessProfileRepository = businessProfileRepository;
    }

    @Override
    @ObservedAction("profile.current")
    public Result execute(UUID accountId) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        return new Result(
                profile,
                profileCapabilityRepository.findAllByAccountId(accountId),
                individualProviderProfileRepository.findByAccountId(accountId),
                businessProfileRepository.findByAccountId(accountId)
        );
    }
}
