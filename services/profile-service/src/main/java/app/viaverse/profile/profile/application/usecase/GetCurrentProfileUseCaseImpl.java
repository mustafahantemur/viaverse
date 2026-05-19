package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.GetCurrentProfileUseCase;
import app.viaverse.profile.profile.application.port.in.ProvisionProfileFromAccountCreatedUseCase;
import app.viaverse.profile.profile.application.port.out.IndividualProviderProfileRepository;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.IdentityProviderGateway;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileTrustSnapshotRepository;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.web.logging.ObservedAction;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentProfileUseCaseImpl implements GetCurrentProfileUseCase {

    private final ProfileRepository profileRepository;
    private final ProfileCapabilityRepository profileCapabilityRepository;
    private final IndividualProviderProfileRepository individualProviderProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final ProfileTrustSnapshotRepository profileTrustSnapshotRepository;
    private final IdentityProviderGateway identityProviderGateway;
    private final ProvisionProfileFromAccountCreatedUseCase provisionProfileFromAccountCreatedUseCase;

    public GetCurrentProfileUseCaseImpl(
            ProfileRepository profileRepository,
            ProfileCapabilityRepository profileCapabilityRepository,
            IndividualProviderProfileRepository individualProviderProfileRepository,
            BusinessProfileRepository businessProfileRepository,
            ProfileTrustSnapshotRepository profileTrustSnapshotRepository,
            IdentityProviderGateway identityProviderGateway,
            ProvisionProfileFromAccountCreatedUseCase provisionProfileFromAccountCreatedUseCase
    ) {
        this.profileRepository = profileRepository;
        this.profileCapabilityRepository = profileCapabilityRepository;
        this.individualProviderProfileRepository = individualProviderProfileRepository;
        this.businessProfileRepository = businessProfileRepository;
        this.profileTrustSnapshotRepository = profileTrustSnapshotRepository;
        this.identityProviderGateway = identityProviderGateway;
        this.provisionProfileFromAccountCreatedUseCase = provisionProfileFromAccountCreatedUseCase;
    }

    @Override
    @ObservedAction("profile.current")
    public Result execute(UUID accountId) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseGet(() -> provisionMissingProfile(accountId));
        return new Result(
                profile,
                profileCapabilityRepository.findAllByAccountId(accountId),
                individualProviderProfileRepository.findByAccountId(accountId),
                businessProfileRepository.findByAccountId(accountId),
                profileTrustSnapshotRepository.findByAccountId(accountId)
        );
    }

    private Profile provisionMissingProfile(UUID accountId) {
        IdentityProviderGateway.AccountSnapshot account = identityProviderGateway.getAccountSnapshot(accountId);
        provisionProfileFromAccountCreatedUseCase.provision(new ProvisionProfileFromAccountCreatedUseCase.Command(
                selfHealEventId(accountId),
                account.createdAt() == null ? Instant.now() : account.createdAt(),
                account.accountId(),
                account.displayName(),
                account.firstName(),
                account.lastName()
        ));
        return profileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalStateException("Profile self-heal did not create profile"));
    }

    private UUID selfHealEventId(UUID accountId) {
        return UUID.nameUUIDFromBytes(("profile-self-heal:" + accountId).getBytes(StandardCharsets.UTF_8));
    }
}
