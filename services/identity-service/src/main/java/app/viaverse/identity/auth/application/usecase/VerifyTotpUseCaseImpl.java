package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.VerifyTotpUseCase;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.PartialAuthTokenService;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.infrastructure.security.AccountSecretCipher;
import app.viaverse.identity.auth.infrastructure.security.TotpService;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class VerifyTotpUseCaseImpl implements VerifyTotpUseCase {

    private final Clock clock;
    private final PartialAuthTokenService partialAuthTokenService;
    private final AuthSessionIssuer sessionIssuer;
    private final TotpService totpService;
    private final AccountSecretCipher accountSecretCipher;
    private final AuthAbuseProtectionService abuseProtectionService;

    public VerifyTotpUseCaseImpl(
            Clock clock,
            PartialAuthTokenService partialAuthTokenService,
            AuthSessionIssuer sessionIssuer,
            TotpService totpService,
            AccountSecretCipher accountSecretCipher,
            AuthAbuseProtectionService abuseProtectionService
    ) {
        this.clock = clock;
        this.partialAuthTokenService = partialAuthTokenService;
        this.sessionIssuer = sessionIssuer;
        this.totpService = totpService;
        this.accountSecretCipher = accountSecretCipher;
        this.abuseProtectionService = abuseProtectionService;
    }

    @Override
    @ObservedAction("auth.totp_verify")
    @AuditEvent(IdentityAuditEventEnum.TWO_FACTOR_VERIFIED)
    public Result execute(Command command) {
        Instant now = clock.instant();
        UUID accountId = partialAuthTokenService.consume(command.partialAuthToken());
        abuseProtectionService.enforceTotpVerify(accountId, command.clientIp());

        Account account = sessionIssuer.activeAccount(accountId);
        if (!account.isTwoFactorEnabled() || account.getTwoFactorSecret() == null) {
            throw IdentityErrors.twoFactorNotEnabled();
        }
        byte[] secret = accountSecretCipher.decrypt(account.getTwoFactorSecret());
        if (!totpService.verify(secret, command.totpCode(), now)) {
            abuseProtectionService.recordTotpVerifyFailure(accountId, command.clientIp());
            throw IdentityErrors.invalidTotp();
        }

        AuthSessionIssuer.Issued issued = sessionIssuer.issue(
                account, command.userAgent(), command.clientIp(), now);
        return new Result(
                AuthNextStepEnum.AUTHENTICATED,
                account.getId(),
                issued.session().getId(),
                issued.accessToken(),
                issued.accessTokenExpiresAt(),
                issued.refreshToken(),
                issued.refreshTokenExpiresAt()
        );
    }
}
