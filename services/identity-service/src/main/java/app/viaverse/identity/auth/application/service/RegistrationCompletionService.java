package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.account.application.port.out.AccountEventPublisher;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.domain.policy.PasswordPolicy;
import app.viaverse.identity.auth.domain.policy.RegistrationPolicy;
import app.viaverse.identity.consent.application.ConsentPolicy;
import app.viaverse.identity.consent.application.port.out.ConsentRecordRepository;
import app.viaverse.identity.consent.domain.ConsentCategoryEnum;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Creates accounts at the end of a registration flow. Two entry points:
 *
 * <ul>
 *   <li>{@link #complete} — the legacy single-identifier path used by
 *       admin invitation registration; consumes a registration token and
 *       attaches the single identifier carried on the token's flow.</li>
 *   <li>{@link #createVerifiedAccount} — the multi-identifier path used
 *       by the draft-based flow ({@code /auth/register/verify-email} and
 *       {@code /auth/register/verify-phone}); password is already
 *       hashed, identifiers are already verified, and the flow(s) are
 *       marked complete by the caller.</li>
 * </ul>
 *
 * <p>Account assembly (consent records, profile, role grants, session
 * issuance, ACCOUNT_CREATED event) is shared via the private
 * {@link #assembleAccount} helper so the two entry points can't drift.
 */
@Service
public class RegistrationCompletionService {
    private static final String CONSENT_SOURCE_REGISTRATION = "registration";

    private final Clock clock;
    private final RegistrationPolicy registrationPolicy;
    private final ConsentPolicy consentPolicy;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationTokenService registrationTokenService;
    private final AuthSessionIssuer sessionIssuer;
    private final AccountRepository accountRepository;
    private final AccountEventPublisher accountEventPublisher;
    private final IdentifierRepository identifierRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final ConsentRecordRepository consentRecordRepository;

    public RegistrationCompletionService(
            Clock clock,
            RegistrationPolicy registrationPolicy,
            ConsentPolicy consentPolicy,
            PasswordPolicy passwordPolicy,
            PasswordEncoder passwordEncoder,
            RegistrationTokenService registrationTokenService,
            AuthSessionIssuer sessionIssuer,
            AccountRepository accountRepository,
            AccountEventPublisher accountEventPublisher,
            IdentifierRepository identifierRepository,
            AuthLoginFlowRepository flowRepository,
            ConsentRecordRepository consentRecordRepository
    ) {
        this.clock = clock;
        this.registrationPolicy = registrationPolicy;
        this.consentPolicy = consentPolicy;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
        this.registrationTokenService = registrationTokenService;
        this.sessionIssuer = sessionIssuer;
        this.accountRepository = accountRepository;
        this.accountEventPublisher = accountEventPublisher;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
        this.consentRecordRepository = consentRecordRepository;
    }

    /**
     * Legacy single-identifier path (admin invitation flow). Consumes the
     * caller's registration token, hashes the supplied password (unless
     * social-verified), and creates the account with one identifier.
     */
    public Completed complete(CompleteRegistrationUseCase.Command command, Set<AccountRoleEnum> roles) {
        Instant now = clock.instant();
        registrationPolicy.validateProfile(command.displayName());
        consentPolicy.validateRequiredConsents(command.acceptedRequiredConsents());

        AuthLoginFlow flow = registrationTokenService.consumeRegistrationToken(command.registrationToken(), now);

        // Social-verified flows (Google / Apple) bypass the password requirement:
        // the IdP has already proved identifier ownership, and the user can add
        // a password later via /me/password if they want a fallback credential.
        // OTP-verified flows always require a password.
        String passwordHash = null;
        if (!flow.isExternalVerified()) {
            passwordPolicy.validate(command.password());
            passwordHash = passwordEncoder.encode(command.password());
        } else if (command.password() != null && !command.password().isBlank()) {
            passwordPolicy.validate(command.password());
            passwordHash = passwordEncoder.encode(command.password());
        }

        return assembleAccount(
                command.displayName(),
                command.firstName(),
                command.lastName(),
                passwordHash,
                List.of(new VerifiedIdentifier(flow.getIdentifierType(), flow.getNormalizedIdentifier())),
                command.acceptedRequiredConsents(),
                command.marketingConsentAccepted(),
                roles,
                List.of(flow),
                command.userAgent(),
                command.clientIp(),
                now
        );
    }

    /**
     * Multi-identifier path used by the new draft-based registration flow.
     * Password is already hashed (stored on the draft); identifiers have
     * already been OTP-verified by the caller. Flows are marked completed
     * here so this method is the single owner of the account-creation
     * transition.
     */
    public Completed createVerifiedAccount(
            String displayName,
            String firstName,
            String lastName,
            String preHashedPassword,
            List<VerifiedIdentifier> identifiers,
            List<ConsentTypeEnum> acceptedRequiredConsents,
            boolean marketingConsentAccepted,
            Set<AccountRoleEnum> roles,
            List<AuthLoginFlow> flowsToComplete,
            String userAgent,
            String clientIp,
            Instant now
    ) {
        registrationPolicy.validateProfile(displayName);
        consentPolicy.validateRequiredConsents(acceptedRequiredConsents);
        return assembleAccount(
                displayName,
                firstName,
                lastName,
                preHashedPassword,
                identifiers,
                acceptedRequiredConsents,
                marketingConsentAccepted,
                roles,
                flowsToComplete,
                userAgent,
                clientIp,
                now
        );
    }

    private Completed assembleAccount(
            String displayName,
            String firstName,
            String lastName,
            String passwordHash,
            List<VerifiedIdentifier> identifiers,
            List<ConsentTypeEnum> acceptedRequiredConsents,
            boolean marketingConsentAccepted,
            Set<AccountRoleEnum> roles,
            List<AuthLoginFlow> flowsToComplete,
            String userAgent,
            String clientIp,
            Instant now
    ) {
        UUID accountId = UUID.randomUUID();
        String trimmedDisplayName = displayName.trim();
        Account account = Account.register(accountId, trimmedDisplayName, now);
        for (AccountRoleEnum role : roles) {
            account.grantRole(role, now);
        }
        if (passwordHash != null) {
            account.setPassword(passwordHash, now);
        }
        if (!isBlank(firstName) || !isBlank(lastName)) {
            account.completeProfile(
                    normalizeOptional(firstName),
                    normalizeOptional(lastName),
                    trimmedDisplayName,
                    now
            );
        }
        account = accountRepository.save(account);

        for (VerifiedIdentifier identifier : identifiers) {
            identifierRepository.save(IdentityIdentifier.verify(
                    UUID.randomUUID(),
                    accountId,
                    identifier.type(),
                    identifier.value(),
                    now
            ));
        }

        for (ConsentTypeEnum type : acceptedRequiredConsents) {
            consentRecordRepository.save(new ConsentRecordRepository.Record(
                    UUID.randomUUID(),
                    accountId,
                    type,
                    ConsentCategoryEnum.REQUIRED_LEGAL.name(),
                    consentPolicy.currentVersion(type),
                    true,
                    now,
                    CONSENT_SOURCE_REGISTRATION
            ));
        }
        consentRecordRepository.save(new ConsentRecordRepository.Record(
                UUID.randomUUID(),
                accountId,
                ConsentTypeEnum.MARKETING_COMMUNICATION,
                ConsentCategoryEnum.OPTIONAL_MARKETING.name(),
                consentPolicy.currentVersion(ConsentTypeEnum.MARKETING_COMMUNICATION),
                marketingConsentAccepted,
                now,
                CONSENT_SOURCE_REGISTRATION
        ));

        for (AuthLoginFlow flow : flowsToComplete) {
            flow.complete(accountId, now);
            flowRepository.save(flow);
        }
        AuthSessionIssuer.Issued issued = sessionIssuer.issue(account, userAgent, clientIp, now);
        accountEventPublisher.publishCreated(account.getId(), account.getDisplayName());
        return new Completed(accountId, issued);
    }

    private static String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record VerifiedIdentifier(IdentifierTypeEnum type, String value) {}

    public record Completed(UUID accountId, AuthSessionIssuer.Issued issued) {}
}
