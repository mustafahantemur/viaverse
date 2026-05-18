package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.EnableIndividualProviderUseCase;
import app.viaverse.profile.profile.application.port.out.IdentityProviderGateway;
import app.viaverse.profile.profile.application.port.out.IndividualProviderProfileRepository;
import app.viaverse.profile.profile.application.port.out.ProfileCapabilityRepository;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.model.IndividualProviderProfile;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.ValidationException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnableIndividualProviderUseCaseImpl implements EnableIndividualProviderUseCase {

    private final ProfileCapabilityRepository capabilityRepository;
    private final IndividualProviderProfileRepository providerProfileRepository;
    private final IdentityProviderGateway identityProviderGateway;
    private final ProfileEventPublisher eventPublisher;
    private final Clock clock;

    public EnableIndividualProviderUseCaseImpl(
            ProfileCapabilityRepository capabilityRepository,
            IndividualProviderProfileRepository providerProfileRepository,
            IdentityProviderGateway identityProviderGateway,
            ProfileEventPublisher eventPublisher,
            Clock clock
    ) {
        this.capabilityRepository = capabilityRepository;
        this.providerProfileRepository = providerProfileRepository;
        this.identityProviderGateway = identityProviderGateway;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.capability.individual-provider.enable")
    @Transactional
    public Result execute(Command command) {
        IdentityProviderGateway.ProviderEnablementFacts facts =
                identityProviderGateway.getProviderEnablementFacts(command.accountId());
        if (!facts.active()) {
            throw new ForbiddenException("Account must be active to enable provider capability");
        }
        if (!facts.hasVerifiedIdentifier()) {
            throw new ValidationException(
                    "A verified email or phone is required",
                    Map.of("identifier", "verified email or phone required")
            );
        }
        if (!facts.currentProviderTermsVersion().equals(command.acceptedProviderTermsVersion())) {
            throw new ValidationException(
                    "Provider terms version is stale",
                    Map.of("acceptedProviderTermsVersion", "must match the current provider terms version")
            );
        }

        identityProviderGateway.acceptProviderTerms(command.accountId(), command.acceptedProviderTermsVersion());
        Instant now = clock.instant();
        var existingCapability = capabilityRepository.findByAccountIdAndCapability(
                        command.accountId(),
                        ProfileCapabilityEnum.INDIVIDUAL_PROVIDER
                );
        boolean changed = existingCapability.map(existing -> !existing.isEnabled()).orElse(true);
        ProfileCapability capability = existingCapability
                .map(existing -> existing.isEnabled() ? existing : capabilityRepository.save(existing.enable(now)))
                .orElseGet(() -> capabilityRepository.save(ProfileCapability.individualProviderEnabled(
                        command.accountId(),
                        now
                )));
        IndividualProviderProfile providerProfile = providerProfileRepository.findByAccountId(command.accountId())
                .map(existing -> providerProfileRepository.save(existing.acceptCurrentTerms(
                        command.acceptedProviderTermsVersion(),
                        now
                )))
                .orElseGet(() -> providerProfileRepository.save(IndividualProviderProfile.create(
                        command.accountId(),
                        command.serviceBlurb(),
                        command.acceptedProviderTermsVersion(),
                        now
                )));
        if (changed) {
            eventPublisher.publishCapabilityEnabled(capability);
        }
        return new Result(capability, providerProfile);
    }
}
