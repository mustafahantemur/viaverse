package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.application.port.out.AccountEventPublisher;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.domain.policy.RegistrationPolicy;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.consent.application.ConsentPolicy;
import app.viaverse.identity.consent.application.port.out.ConsentRecordRepository;
import app.viaverse.identity.consent.domain.ConsentCategoryEnum;
import app.viaverse.identity.consent.domain.ConsentInput;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.shared.logging.ObservedAction;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CompleteRegistrationUseCaseImpl implements CompleteRegistrationUseCase {

    private static final String CONSENT_SOURCE_REGISTRATION = "registration";
    private final Clock clock;
    private final AuthProperties properties;
    private final RegistrationPolicy registrationPolicy;
    private final ConsentPolicy consentPolicy;
    private final RegistrationTokenService registrationTokenService;
    private final AuthSessionIssuer sessionIssuer;
    private final AccountRepository accountRepository;
    private final AccountEventPublisher accountEventPublisher;
    private final IdentifierRepository identifierRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final ConsentRecordRepository consentRecordRepository;

    public CompleteRegistrationUseCaseImpl(
            Clock clock,
            AuthProperties properties,
            RegistrationPolicy registrationPolicy,
            ConsentPolicy consentPolicy,
            RegistrationTokenService registrationTokenService,
            AuthSessionIssuer sessionIssuer,
            AccountRepository accountRepository,
            AccountEventPublisher accountEventPublisher,
            IdentifierRepository identifierRepository,
            AuthLoginFlowRepository flowRepository,
            ConsentRecordRepository consentRecordRepository
    ) {
        this.clock = clock;
        this.properties = properties;
        this.registrationPolicy = registrationPolicy;
        this.consentPolicy = consentPolicy;
        this.registrationTokenService = registrationTokenService;
        this.sessionIssuer = sessionIssuer;
        this.accountRepository = accountRepository;
        this.accountEventPublisher = accountEventPublisher;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
        this.consentRecordRepository = consentRecordRepository;
    }

    @Override
    @ObservedAction("auth.register")
    @AuditEvent(IdentityAuditEventEnum.ACCOUNT_CREATED)
    public Result execute(Command command) {
        Instant now = clock.instant();
        registrationPolicy.validateProfile(command.displayName());
        consentPolicy.validateRequiredConsents(command.requiredConsents());

        AuthLoginFlow flow = registrationTokenService.consumeRegistrationToken(command.registrationToken(), now);

        UUID accountId = UUID.randomUUID();
        String displayName = command.displayName().trim();
        Account account = Account.register(accountId, displayName, now);
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

        for (ConsentInput consent : command.requiredConsents()) {
            consentRecordRepository.save(new ConsentRecordRepository.Record(
                    UUID.randomUUID(),
                    accountId,
                    consent.type(),
                    ConsentCategoryEnum.REQUIRED_LEGAL.name(),
                    consent.version(),
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
                properties.getConsent().getMarketingVersion(),
                command.marketingConsentAccepted(),
                now,
                CONSENT_SOURCE_REGISTRATION
        ));

        flow.complete(accountId, now);
        flowRepository.save(flow);
        AuthSessionIssuer.Issued issued = sessionIssuer.issue(account, command.userAgent(), command.clientIp(), now);
        accountEventPublisher.publishCreated(account.getId(), account.getDisplayName());
        return new Result(
                accountId,
                issued.session().getId(),
                issued.accessToken(),
                issued.accessTokenExpiresAt(),
                issued.refreshToken(),
                issued.refreshTokenExpiresAt()
        );
    }

    private static String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
