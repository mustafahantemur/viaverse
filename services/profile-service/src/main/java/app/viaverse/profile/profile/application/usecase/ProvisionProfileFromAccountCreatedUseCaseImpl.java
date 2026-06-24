package app.viaverse.profile.profile.application.usecase;

import app.viaverse.contracts.identity.account.IdentityAccountEventTypes;
import app.viaverse.profile.config.ProfileProperties;
import app.viaverse.profile.profile.application.port.in.ProvisionProfileFromAccountCreatedUseCase;
import app.viaverse.profile.profile.application.port.out.ConsumedEventRepository;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.application.port.out.ProfileRepository;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.profile.profile.domain.policy.ProfilePolicy;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvisionProfileFromAccountCreatedUseCaseImpl implements ProvisionProfileFromAccountCreatedUseCase {

    private final ProfileRepository profileRepository;
    private final ProfileCapabilityRepository profileCapabilityRepository;
    private final ConsumedEventRepository consumedEventRepository;
    private final ProfileEventPublisher profileEventPublisher;
    private final ProfileProperties properties;
    private final Clock clock;

    public ProvisionProfileFromAccountCreatedUseCaseImpl(
            ProfileRepository profileRepository,
            ProfileCapabilityRepository profileCapabilityRepository,
            ConsumedEventRepository consumedEventRepository,
            ProfileEventPublisher profileEventPublisher,
            ProfileProperties properties,
            Clock clock
    ) {
        this.profileRepository = profileRepository;
        this.profileCapabilityRepository = profileCapabilityRepository;
        this.consumedEventRepository = consumedEventRepository;
        this.profileEventPublisher = profileEventPublisher;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.provision")
    @Transactional
    public Result provision(Command command) {
        Objects.requireNonNull(command, "command");
        if (consumedEventRepository.existsByEventId(command.eventId())) {
            return new Result(command.accountId(), false);
        }
        if (profileRepository.findByAccountId(command.accountId()).isPresent()) {
            recordConsumed(command);
            return new Result(command.accountId(), false);
        }

        Profile profile = Profile.provision(
                command.accountId(),
                command.displayName(),
                command.firstName(),
                command.lastName(),
                properties.getLocale(),
                properties.getTimezone(),
                command.occurredAt()
        );
        Profile completed = profile.withCompletenessScore(ProfilePolicy.computeCompleteness(profile, false, false));
        Profile saved = profileRepository.save(completed);
        profileCapabilityRepository.save(ProfileCapability.customerEnabled(saved.getAccountId(), command.occurredAt()));
        profileEventPublisher.publishCreated(saved);
        recordConsumed(command);
        return new Result(saved.getAccountId(), true);
    }

    private void recordConsumed(Command command) {
        consumedEventRepository.record(
                command.eventId(),
                IdentityAccountEventTypes.ACCOUNT_CREATED_V1,
                Instant.now(clock)
        );
    }
}
