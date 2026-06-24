package app.viaverse.profile.profile.application.usecase;

import app.viaverse.profile.profile.application.port.in.SubmitBusinessOnboardingUseCase;
import app.viaverse.profile.profile.application.port.out.BusinessProfileRepository;
import app.viaverse.profile.profile.application.port.out.IdentityProviderGateway;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.shared.kernel.error.ValidationException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmitBusinessOnboardingUseCaseImpl implements SubmitBusinessOnboardingUseCase {

    private final BusinessProfileRepository repository;
    private final IdentityProviderGateway identityProviderGateway;
    private final ProfileEventPublisher eventPublisher;
    private final Clock clock;

    public SubmitBusinessOnboardingUseCaseImpl(
            BusinessProfileRepository repository,
            IdentityProviderGateway identityProviderGateway,
            ProfileEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.identityProviderGateway = identityProviderGateway;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("profile.business.submit")
    @Transactional
    public BusinessProfile execute(Command command) {
        BusinessProfile current = repository.findByAccountId(command.accountId())
                .orElseThrow(() -> new NotFoundException("Business profile not found"));
        IdentityProviderGateway.BusinessEnablementFacts facts =
                identityProviderGateway.getBusinessEnablementFacts(command.accountId());
        if (!facts.active()) {
            throw new ForbiddenException("Account must be active to submit a business profile");
        }
        if (!facts.hasVerifiedIdentifier()) {
            throw new ValidationException(
                    "A verified email or phone is required",
                    Map.of("identifier", "verified email or phone required")
            );
        }
        if (!facts.currentBusinessTermsVersion().equals(command.acceptedBusinessTermsVersion())) {
            throw new ValidationException(
                    "Business terms version is stale",
                    Map.of("acceptedBusinessTermsVersion", "must match the current business terms version")
            );
        }
        Map<String, String> submissionErrors = current.submissionErrors();
        if (!submissionErrors.isEmpty()) {
            throw new ValidationException("Business profile is incomplete", submissionErrors);
        }
        identityProviderGateway.acceptBusinessTerms(command.accountId(), command.acceptedBusinessTermsVersion());
        BusinessProfile submitted = repository.save(current.submit(
                command.acceptedBusinessTermsVersion(),
                clock.instant()
        ));
        eventPublisher.publishBusinessSubmitted(submitted);
        return submitted;
    }
}
