package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.application.port.out.BackupCodeRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.DisableTwoFactorUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.BackupCodeService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.infrastructure.security.AccountSecretCipher;
import app.viaverse.identity.auth.infrastructure.security.TotpService;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisableTwoFactorUseCaseImpl implements DisableTwoFactorUseCase {

    private final Clock clock;
    private final AuthSessionIssuer sessionIssuer;
    private final AccountRepository accountRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final OtpChallengeService otpChallengeService;
    private final BackupCodeService backupCodeService;
    private final BackupCodeRepository backupCodeRepository;
    private final TotpService totpService;
    private final AccountSecretCipher cipher;
    private final AuthAbuseProtectionService abuseProtectionService;

    public DisableTwoFactorUseCaseImpl(
            Clock clock,
            AuthSessionIssuer sessionIssuer,
            AccountRepository accountRepository,
            AuthLoginFlowRepository flowRepository,
            OtpChallengeService otpChallengeService,
            BackupCodeService backupCodeService,
            BackupCodeRepository backupCodeRepository,
            TotpService totpService,
            AccountSecretCipher cipher,
            AuthAbuseProtectionService abuseProtectionService
    ) {
        this.clock = clock;
        this.sessionIssuer = sessionIssuer;
        this.accountRepository = accountRepository;
        this.flowRepository = flowRepository;
        this.otpChallengeService = otpChallengeService;
        this.backupCodeService = backupCodeService;
        this.backupCodeRepository = backupCodeRepository;
        this.totpService = totpService;
        this.cipher = cipher;
        this.abuseProtectionService = abuseProtectionService;
    }

    @Override
    @ObservedAction("auth.2fa.disable")
    @AuditEvent(IdentityAuditEventEnum.TWO_FACTOR_DISABLED)
    @Transactional
    public void execute(Command command) {
        Instant now = clock.instant();
        abuseProtectionService.enforceTwoFactorOp(command.accountId());
        Account account = sessionIssuer.activeAccount(command.accountId());
        if (!account.isTwoFactorEnabled() || account.getTwoFactorSecret() == null) {
            throw IdentityErrors.twoFactorNotEnabled();
        }

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

        // Either a current TOTP code OR a single-use backup code is sufficient
        // — important escape hatch for users who lost their authenticator.
        boolean totpOk = command.totpCode() != null
                && !command.totpCode().isBlank()
                && totpService.verify(cipher.decrypt(account.getTwoFactorSecret()), command.totpCode(), now);
        boolean backupOk = !totpOk && backupCodeService.consume(account.getId(), command.backupCode(), now);
        if (!totpOk && !backupOk) {
            throw IdentityErrors.invalidTotp();
        }

        account.disableTwoFactor(now);
        accountRepository.save(account);
        backupCodeRepository.deleteByAccountId(account.getId());
        flow.complete(account.getId(), now);
        flowRepository.save(flow);
    }
}
