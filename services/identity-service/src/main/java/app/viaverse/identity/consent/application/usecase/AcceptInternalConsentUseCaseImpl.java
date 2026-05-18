package app.viaverse.identity.consent.application.usecase;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.consent.application.ConsentPolicy;
import app.viaverse.identity.consent.application.port.in.AcceptInternalConsentUseCase;
import app.viaverse.identity.consent.application.port.out.ConsentRecordRepository;
import app.viaverse.identity.consent.domain.ConsentCategoryEnum;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.shared.kernel.error.ValidationException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcceptInternalConsentUseCaseImpl implements AcceptInternalConsentUseCase {

    private static final String DEFAULT_SOURCE = "internal";

    private final Clock clock;
    private final AccountRepository accountRepository;
    private final ConsentPolicy consentPolicy;
    private final ConsentRecordRepository consentRecordRepository;

    public AcceptInternalConsentUseCaseImpl(
            Clock clock,
            AccountRepository accountRepository,
            ConsentPolicy consentPolicy,
            ConsentRecordRepository consentRecordRepository
    ) {
        this.clock = clock;
        this.accountRepository = accountRepository;
        this.consentPolicy = consentPolicy;
        this.consentRecordRepository = consentRecordRepository;
    }

    @Override
    @ObservedAction("identity.internal.consent.accept")
    @AuditEvent(IdentityAuditEventEnum.CONSENT_ACCEPTED)
    @Transactional
    public Result execute(Command command) {
        accountRepository.findById(command.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (!isCapabilityTerms(command.type())) {
            throw new ValidationException(
                    "Internal consent endpoint only supports capability terms",
                    Map.of("type", "must be PROVIDER_TERMS or BUSINESS_TERMS")
            );
        }
        String currentVersion = consentPolicy.currentVersion(command.type());
        if (!currentVersion.equals(command.version())) {
            throw new ValidationException(
                    "Consent version is stale",
                    Map.of("version", "must match the current published version")
            );
        }
        return consentRecordRepository.findByAccountIdAndTypeAndVersion(
                        command.accountId(),
                        command.type(),
                        command.version()
                )
                .map(existing -> new Result(command.accountId(), command.type(), command.version(), false))
                .orElseGet(() -> create(command, clock.instant()));
    }

    private Result create(Command command, Instant now) {
        consentRecordRepository.save(new ConsentRecordRepository.Record(
                UUID.randomUUID(),
                command.accountId(),
                command.type(),
                ConsentCategoryEnum.CAPABILITY_TERMS.name(),
                command.version(),
                true,
                now,
                command.source() == null || command.source().isBlank() ? DEFAULT_SOURCE : command.source()
        ));
        return new Result(command.accountId(), command.type(), command.version(), true);
    }

    private boolean isCapabilityTerms(ConsentTypeEnum type) {
        return type == ConsentTypeEnum.PROVIDER_TERMS || type == ConsentTypeEnum.BUSINESS_TERMS;
    }
}
