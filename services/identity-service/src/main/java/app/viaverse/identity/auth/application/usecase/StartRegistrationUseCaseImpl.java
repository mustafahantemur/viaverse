package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.StartRegistrationUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.port.out.RegistrationDraftStore;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.RegistrationDraft;
import app.viaverse.identity.auth.domain.policy.PasswordPolicy;
import app.viaverse.identity.auth.domain.policy.RegistrationPolicy;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.consent.application.ConsentPolicy;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Form-first registration: validate the entire submission up front so we
 * never strand the user halfway through OTP exchange because their
 * password didn't meet the policy.
 *
 * <p>The flow is:
 *
 * <ol>
 *   <li>Validate display name, password policy, consents.</li>
 *   <li>Normalise email + (optional) phone via libphonenumber.</li>
 *   <li>Reject if either identifier is already attached to an account
 *       — better to show a clear error before sending an OTP.</li>
 *   <li>Hash the password (so plaintext never reaches the cache).</li>
 *   <li>Issue an {@link AuthLoginFlow} for the email + dispatch the OTP.</li>
 *   <li>Persist the draft with a TTL so an abandoned signup auto-cleans.</li>
 * </ol>
 */
@Service
public class StartRegistrationUseCaseImpl implements StartRegistrationUseCase {

    private static final Duration DRAFT_TTL = Duration.ofMinutes(30);

    private final Clock clock;
    private final AuthProperties properties;
    private final RegistrationPolicy registrationPolicy;
    private final PasswordPolicy passwordPolicy;
    private final ConsentPolicy consentPolicy;
    private final PasswordEncoder passwordEncoder;
    private final IdentifierNormalizer identifierNormalizer;
    private final IdentifierRepository identifierRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final OtpChallengeService otpChallengeService;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final RegistrationDraftStore draftStore;

    public StartRegistrationUseCaseImpl(
            Clock clock,
            AuthProperties properties,
            RegistrationPolicy registrationPolicy,
            PasswordPolicy passwordPolicy,
            ConsentPolicy consentPolicy,
            PasswordEncoder passwordEncoder,
            IdentifierNormalizer identifierNormalizer,
            IdentifierRepository identifierRepository,
            AuthLoginFlowRepository flowRepository,
            OtpChallengeService otpChallengeService,
            AuthAbuseProtectionService abuseProtectionService,
            RegistrationDraftStore draftStore
    ) {
        this.clock = clock;
        this.properties = properties;
        this.registrationPolicy = registrationPolicy;
        this.passwordPolicy = passwordPolicy;
        this.consentPolicy = consentPolicy;
        this.passwordEncoder = passwordEncoder;
        this.identifierNormalizer = identifierNormalizer;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
        this.otpChallengeService = otpChallengeService;
        this.abuseProtectionService = abuseProtectionService;
        this.draftStore = draftStore;
    }

    @Override
    @ObservedAction("auth.register.start")
    public Result execute(Command command) {
        Instant now = clock.instant();

        registrationPolicy.validateProfile(command.displayName());
        passwordPolicy.validate(command.password());
        consentPolicy.validateRequiredConsents(command.acceptedRequiredConsents());

        NormalizedIdentifier email = identifierNormalizer.normalize(command.email());
        if (email.type() != IdentifierTypeEnum.EMAIL) {
            throw IdentityErrors.invalidEmailIdentifier();
        }
        if (identifierRepository.findByTypeAndValue(email.type(), email.value()).isPresent()) {
            throw IdentityErrors.emailAlreadyRegistered();
        }

        String normalizedPhone = null;
        if (command.phone() != null && !command.phone().isBlank()) {
            NormalizedIdentifier phone = identifierNormalizer.normalize(command.phone());
            if (phone.type() != IdentifierTypeEnum.PHONE) {
                throw IdentityErrors.invalidIdentifier();
            }
            if (identifierRepository.findByTypeAndValue(phone.type(), phone.value()).isPresent()) {
                throw IdentityErrors.phoneAlreadyRegistered();
            }
            normalizedPhone = phone.value();
        }

        // Rate-limit on the email identifier — this is the bucket /auth/start
        // would have used. Separate from the OTP-resend cooldown, which we
        // apply right before issuing the OTP below.
        abuseProtectionService.enforceStart(email, command.clientIp(), command.clientFingerprint());
        abuseProtectionService.enforceOtpResendCooldown(email);

        Instant otpExpiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlow emailFlow = flowRepository.save(AuthLoginFlow.issue(
                UUID.randomUUID(),
                LoginFlowPurposeEnum.REGISTRATION,
                email.type(),
                email.value(),
                null,
                otpExpiresAt,
                now
        ));
        otpChallengeService.issue(emailFlow.getId(), email, otpExpiresAt, now);

        String passwordHash = passwordEncoder.encode(command.password());
        RegistrationDraft draft = RegistrationDraft.draft(
                UUID.randomUUID(),
                email.value(),
                normalizedPhone,
                command.displayName(),
                command.firstName(),
                command.lastName(),
                passwordHash,
                command.acceptedRequiredConsents(),
                command.marketingConsentAccepted(),
                emailFlow.getId(),
                now
        );
        draftStore.save(draft, DRAFT_TTL);

        return new Result(
                draft.getId(),
                emailFlow.getId(),
                otpExpiresAt,
                normalizedPhone != null
        );
    }
}
