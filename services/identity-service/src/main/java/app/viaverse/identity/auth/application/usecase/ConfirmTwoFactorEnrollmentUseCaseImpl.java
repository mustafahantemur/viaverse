package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.ConfirmTwoFactorEnrollmentUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.BackupCodeService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.application.service.TwoFactorEnrollmentService;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.infrastructure.security.AccountSecretCipher;
import app.viaverse.identity.auth.infrastructure.security.TotpService;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmTwoFactorEnrollmentUseCaseImpl implements ConfirmTwoFactorEnrollmentUseCase {

    private final Clock clock;
    private final AuthSessionIssuer sessionIssuer;
    private final AccountRepository accountRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final OtpChallengeService otpChallengeService;
    private final TwoFactorEnrollmentService enrollmentService;
    private final BackupCodeService backupCodeService;
    private final TotpService totpService;
    private final AccountSecretCipher cipher;
    private final AuthAbuseProtectionService abuseProtectionService;

    public ConfirmTwoFactorEnrollmentUseCaseImpl(
            Clock clock,
            AuthSessionIssuer sessionIssuer,
            AccountRepository accountRepository,
            AuthLoginFlowRepository flowRepository,
            OtpChallengeService otpChallengeService,
            TwoFactorEnrollmentService enrollmentService,
            BackupCodeService backupCodeService,
            TotpService totpService,
            AccountSecretCipher cipher,
            AuthAbuseProtectionService abuseProtectionService
    ) {
        this.clock = clock;
        this.sessionIssuer = sessionIssuer;
        this.accountRepository = accountRepository;
        this.flowRepository = flowRepository;
        this.otpChallengeService = otpChallengeService;
        this.enrollmentService = enrollmentService;
        this.backupCodeService = backupCodeService;
        this.totpService = totpService;
        this.cipher = cipher;
        this.abuseProtectionService = abuseProtectionService;
    }

    @Override
    @ObservedAction("auth.2fa.enroll_confirm")
    @AuditEvent(IdentityAuditEventEnum.TWO_FACTOR_ENABLED)
    @Transactional
    public Result execute(Command command) {
        Instant now = clock.instant();
        abuseProtectionService.enforceTwoFactorOp(command.accountId());
        Account account = sessionIssuer.activeAccount(command.accountId());
        if (account.isTwoFactorEnabled()) {
            throw IdentityErrors.twoFactorAlreadyEnabled();
        }
        byte[] pendingSecret = enrollmentService.findPending(account.getId())
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(Map.of("twoFactor", "no pending enrollment")));

        AuthLoginFlow flow = flowRepository.findById(command.flowId())
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(Map.of("flowId", "is invalid")));
        if (flow.getPurpose() != LoginFlowPurposeEnum.TWO_FACTOR_SETUP
                || flow.getAccountId() == null
                || !flow.getAccountId().equals(account.getId())) {
            throw IdentityErrors.invalidAuthFlow(Map.of("flowId", "is not a 2FA setup flow for this account"));
        }
        abuseProtectionService.enforceOtpAttempt(flow, command.clientIp());
        if (!otpChallengeService.verifyOtpForFlow(flow, command.otp(), now)) {
            throw IdentityErrors.invalidOtp();
        }
        if (!totpService.verify(pendingSecret, command.totpCode(), now)) {
            throw IdentityErrors.invalidTotp();
        }

        account.enableTwoFactor(cipher.encrypt(pendingSecret), now);
        accountRepository.save(account);
        enrollmentService.clearPending(account.getId());
        flow.complete(account.getId(), now);
        flowRepository.save(flow);

        List<String> backupCodes = backupCodeService.issueBatch(account.getId(), now);
        return new Result(backupCodes);
    }
}

