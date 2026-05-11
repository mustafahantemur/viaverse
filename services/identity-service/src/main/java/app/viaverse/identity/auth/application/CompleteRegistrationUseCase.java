package app.viaverse.identity.auth.application;

import app.viaverse.identity.account.domain.AccountStatus;
import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.account.infrastructure.persistence.repository.IdentityAccountJpaRepository;
import app.viaverse.identity.auth.api.dto.AuthResponse;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.policy.RegistrationPolicy;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthLoginFlowJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.IdentityIdentifierJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.IdentityIdentifierJpaRepository;
import app.viaverse.identity.consent.application.ConsentPolicy;
import app.viaverse.identity.consent.domain.ConsentCategory;
import app.viaverse.identity.consent.domain.ConsentInput;
import app.viaverse.identity.consent.domain.ConsentType;
import app.viaverse.identity.consent.infrastructure.persistence.entity.ConsentRecordJpaEntity;
import app.viaverse.identity.consent.infrastructure.persistence.repository.ConsentRecordJpaRepository;
import app.viaverse.identity.shared.audit.IdentityAuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEvents;
import app.viaverse.identity.shared.error.IdentityException;
import app.viaverse.observability.audit.AuditLogger;
import app.viaverse.observability.logging.SafeLogFields;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteRegistrationUseCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteRegistrationUseCase.class);

    private final RegistrationPolicy registrationPolicy;
    private final ConsentPolicy consentPolicy;
    private final RegistrationTokenService registrationTokenService;
    private final AuthSessionIssuer sessionIssuer;
    private final IdentityAccountJpaRepository accountRepository;
    private final IdentityIdentifierJpaRepository identifierRepository;
    private final ConsentRecordJpaRepository consentRecordRepository;
    private final AuditLogger auditLogger;

    public CompleteRegistrationUseCase(
            RegistrationPolicy registrationPolicy,
            ConsentPolicy consentPolicy,
            RegistrationTokenService registrationTokenService,
            AuthSessionIssuer sessionIssuer,
            IdentityAccountJpaRepository accountRepository,
            IdentityIdentifierJpaRepository identifierRepository,
            ConsentRecordJpaRepository consentRecordRepository,
            AuditLogger auditLogger
    ) {
        this.registrationPolicy = registrationPolicy;
        this.consentPolicy = consentPolicy;
        this.registrationTokenService = registrationTokenService;
        this.sessionIssuer = sessionIssuer;
        this.accountRepository = accountRepository;
        this.identifierRepository = identifierRepository;
        this.consentRecordRepository = consentRecordRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional
    public AuthResponse complete(
            String registrationToken,
            String displayName,
            String firstName,
            String lastName,
            List<ConsentInput> requiredConsents,
            boolean marketingConsentAccepted,
            String userAgent
    ) {
        Instant now = Instant.now();
        AuthLoginFlowJpaEntity flow;
        try {
            registrationPolicy.validateProfile(displayName);
            consentPolicy.validateRequiredConsents(requiredConsents);
            flow = registrationTokenService.consumeRegistrationToken(registrationToken, now);
        } catch (IdentityException exception) {
            LOGGER.atWarn()
                    .addKeyValue("event.action", "auth.register")
                    .addKeyValue("event.outcome", "failure")
                    .addKeyValue("error.code", exception.errorCode())
                    .log("auth.register failed");
            throw exception;
        }

        UUID accountId = UUID.randomUUID();
        IdentityAccountJpaEntity account = accountRepository.save(new IdentityAccountJpaEntity(
                accountId,
                AccountStatus.ACTIVE,
                displayName.trim(),
                normalizeOptional(firstName),
                normalizeOptional(lastName),
                true,
                now,
                now
        ));
        identifierRepository.save(new IdentityIdentifierJpaEntity(
                UUID.randomUUID(),
                accountId,
                flow.getIdentifierType(),
                flow.getNormalizedIdentifier(),
                now,
                now
        ));

        for (ConsentInput consent : requiredConsents) {
            consentRecordRepository.save(new ConsentRecordJpaEntity(
                    UUID.randomUUID(),
                    accountId,
                    consent.type(),
                    ConsentCategory.REQUIRED_LEGAL,
                    consent.version(),
                    true,
                    now,
                    "registration"
            ));
        }
        consentRecordRepository.save(new ConsentRecordJpaEntity(
                UUID.randomUUID(),
                accountId,
                ConsentType.MARKETING_COMMUNICATION,
                ConsentCategory.OPTIONAL_MARKETING,
                "v1",
                marketingConsentAccepted,
                now,
                "registration"
        ));
        flow.complete(accountId, now);
        IdentityAuditEvents.recordAccountSecurityEvent(auditLogger, accountId, IdentityAuditEvent.REGISTER);
        LOGGER.atInfo()
                .addKeyValue("event.action", "auth.register")
                .addKeyValue("event.outcome", "success")
                .addKeyValue("auth.flow_id", flow.getId())
                .addKeyValue("auth.identifier_type", flow.getIdentifierType())
                .addKeyValue("auth.identifier_masked", SafeLogFields.maskIdentifier(flow.getNormalizedIdentifier()))
                .addKeyValue("user.id", accountId)
                .log("auth.register succeeded");
        return sessionIssuer.issue(account, userAgent, now);
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
