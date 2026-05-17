package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.StartTwoFactorEnrollmentUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.application.service.TwoFactorEnrollmentService;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.infrastructure.security.TotpService;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

@Service
public class StartTwoFactorEnrollmentUseCaseImpl implements StartTwoFactorEnrollmentUseCase {
    private static final String ISSUER = "Viaverse";

    private final Clock clock;
    private final AuthProperties properties;
    private final AuthSessionIssuer sessionIssuer;
    private final IdentifierRepository identifierRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final OtpChallengeService otpChallengeService;
    private final TwoFactorEnrollmentService enrollmentService;
    private final TotpService totpService;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final Base32 base32 = new Base32();

    public StartTwoFactorEnrollmentUseCaseImpl(
            Clock clock,
            AuthProperties properties,
            AuthSessionIssuer sessionIssuer,
            IdentifierRepository identifierRepository,
            AuthLoginFlowRepository flowRepository,
            OtpChallengeService otpChallengeService,
            TwoFactorEnrollmentService enrollmentService,
            TotpService totpService,
            AuthAbuseProtectionService abuseProtectionService
    ) {
        this.clock = clock;
        this.properties = properties;
        this.sessionIssuer = sessionIssuer;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
        this.otpChallengeService = otpChallengeService;
        this.enrollmentService = enrollmentService;
        this.totpService = totpService;
        this.abuseProtectionService = abuseProtectionService;
    }

    @Override
    @ObservedAction("auth.2fa.enroll_start")
    @AuditEvent(IdentityAuditEventEnum.TWO_FACTOR_ENROLL_STARTED)
    public Result execute(Command command) {
        Instant now = clock.instant();
        abuseProtectionService.enforceTwoFactorOp(command.accountId());
        Account account = sessionIssuer.activeAccount(command.accountId());
        if (account.isTwoFactorEnabled()) {
            throw IdentityErrors.twoFactorAlreadyEnabled();
        }
        IdentityIdentifier primary = pickPrimaryIdentifier(command.accountId());

        byte[] secret = totpService.generateSecret();
        enrollmentService.savePending(account.getId(), secret);

        NormalizedIdentifier normalized = new NormalizedIdentifier(primary.identifierType(), primary.normalizedIdentifier());
        Instant expiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlow flow = flowRepository.save(AuthLoginFlow.issue(
                UUID.randomUUID(),
                LoginFlowPurposeEnum.TWO_FACTOR_SETUP,
                normalized.type(),
                normalized.value(),
                account.getId(),
                expiresAt,
                now
        ));
        otpChallengeService.issue(flow.getId(), normalized, expiresAt, now);

        String accountLabel = account.getDisplayName() + " (" + primary.normalizedIdentifier() + ")";
        return new Result(
                flow.getId(),
                primary.identifierType(),
                maskIdentifier(primary),
                expiresAt,
                base32.encodeAsString(secret).replace("=", ""),
                totpService.provisioningUri(secret, ISSUER, accountLabel)
        );
    }

    private IdentityIdentifier pickPrimaryIdentifier(UUID accountId) {
        List<IdentityIdentifier> all = identifierRepository.findByAccountId(accountId);
        return all.stream()
                .filter(id -> id.identifierType() != IdentifierTypeEnum.SOCIAL)
                .min(Comparator.comparing((IdentityIdentifier id) -> id.identifierType() == IdentifierTypeEnum.EMAIL ? 0 : 1)
                        .thenComparing(IdentityIdentifier::createdAt))
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(
                        java.util.Map.of("identifier", "no verified email or phone on account")));
    }

    private String maskIdentifier(IdentityIdentifier identifier) {
        String value = identifier.normalizedIdentifier();
        if (identifier.identifierType() == IdentifierTypeEnum.EMAIL) {
            int at = value.indexOf('@');
            if (at <= 1) {
                return "***" + value.substring(at);
            }
            return value.charAt(0) + "***" + value.substring(at);
        }
        if (value.length() <= 4) {
            return "***" + value;
        }
        return "***" + value.substring(value.length() - 4);
    }
}
