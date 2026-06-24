package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.PasswordLoginUseCase;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.PartialAuthTokenService;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.web.logging.ObservedAction;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordLoginUseCaseImpl implements PasswordLoginUseCase {

    private final Clock clock;
    private final IdentifierNormalizer identifierNormalizer;
    private final IdentifierRepository identifierRepository;
    private final AuthSessionIssuer sessionIssuer;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final PasswordEncoder passwordEncoder;
    private final PartialAuthTokenService partialAuthTokenService;

    public PasswordLoginUseCaseImpl(
            Clock clock,
            IdentifierNormalizer identifierNormalizer,
            IdentifierRepository identifierRepository,
            AuthSessionIssuer sessionIssuer,
            AuthAbuseProtectionService abuseProtectionService,
            PasswordEncoder passwordEncoder,
            PartialAuthTokenService partialAuthTokenService
    ) {
        this.clock = clock;
        this.identifierNormalizer = identifierNormalizer;
        this.identifierRepository = identifierRepository;
        this.sessionIssuer = sessionIssuer;
        this.abuseProtectionService = abuseProtectionService;
        this.passwordEncoder = passwordEncoder;
        this.partialAuthTokenService = partialAuthTokenService;
    }

    @Override
    @ObservedAction("auth.password_login")
    @AuditEvent(IdentityAuditEventEnum.PASSWORD_LOGIN_SUCCEEDED)
    public Result execute(Command command) {
        Instant now = clock.instant();
        NormalizedIdentifier normalized = identifierNormalizer.normalize(command.identifier());
        // Gate first (read-only) so a brute-forced account is blocked before we
        // even look at the password. Only confirmed failures bump the counter
        // below, so a healthy user who signs in often never burns their budget.
        abuseProtectionService.enforcePasswordLogin(normalized, command.clientIp());

        Optional<IdentityIdentifier> identifier =
                identifierRepository.findByTypeAndValue(normalized.type(), normalized.value());
        if (identifier.isEmpty()) {
            // Same error as "wrong password" — no identifier enumeration.
            abuseProtectionService.recordPasswordLoginFailure(normalized, command.clientIp());
            throw IdentityErrors.invalidCredentials();
        }
        Account account;
        try {
            account = sessionIssuer.activeAccount(identifier.get().accountId());
        } catch (RuntimeException exception) {
            abuseProtectionService.recordPasswordLoginFailure(normalized, command.clientIp());
            throw IdentityErrors.invalidCredentials();
        }
        if (!account.hasPassword() || !passwordEncoder.matches(command.password(), account.getPasswordHash())) {
            abuseProtectionService.recordPasswordLoginFailure(normalized, command.clientIp());
            throw IdentityErrors.invalidCredentials();
        }

        if (account.isTwoFactorEnabled()) {
            PartialAuthTokenService.Issued partial = partialAuthTokenService.issue(account.getId(), now);
            return new Result(
                    AuthNextStepEnum.TOTP_REQUIRED,
                    account.getId(),
                    null, null, null, null, null,
                    partial.token(),
                    partial.expiresAt()
            );
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
                issued.refreshTokenExpiresAt(),
                null, null
        );
    }
}

