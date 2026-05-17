package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.account.application.port.out.AccountEventPublisher;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
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
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            // User opted to also set a password during social registration —
            // still enforce the policy.
            passwordPolicy.validate(command.password());
            passwordHash = passwordEncoder.encode(command.password());
        }

        UUID accountId = UUID.randomUUID();
        String displayName = command.displayName().trim();
        Account account = Account.register(accountId, displayName, now);
        for (AccountRoleEnum role : roles) {
            account.grantRole(role, now);
        }
        if (passwordHash != null) {
            account.setPassword(passwordHash, now);
        }
        if (!isBlank(command.firstName()) || !isBlank(command.lastName())) {
            account.completeProfile(
                    normalizeOptional(command.firstName()),
                    normalizeOptional(command.lastName()),
                    displayName,
                    now
            );
        }
        account = accountRepository.save(account);

        identifierRepository.save(IdentityIdentifier.verify(
                UUID.randomUUID(),
                accountId,
                flow.getIdentifierType(),
                flow.getNormalizedIdentifier(),
                now
        ));

        for (ConsentTypeEnum type : command.acceptedRequiredConsents()) {
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
                command.marketingConsentAccepted(),
                now,
                CONSENT_SOURCE_REGISTRATION
        ));

        flow.complete(accountId, now);
        flowRepository.save(flow);
        AuthSessionIssuer.Issued issued = sessionIssuer.issue(account, command.userAgent(), command.clientIp(), now);
        accountEventPublisher.publishCreated(account.getId(), account.getDisplayName());
        return new Completed(accountId, issued);
    }

    private static String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record Completed(UUID accountId, AuthSessionIssuer.Issued issued) {}
}
