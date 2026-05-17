package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.CompletePasswordResetUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.policy.PasswordPolicy;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompletePasswordResetUseCaseImpl implements CompletePasswordResetUseCase {

    private final Clock clock;
    private final RegistrationTokenService registrationTokenService;
    private final AuthLoginFlowRepository flowRepository;
    private final AuthSessionIssuer sessionIssuer;
    private final AccountRepository accountRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;

    public CompletePasswordResetUseCaseImpl(
            Clock clock,
            RegistrationTokenService registrationTokenService,
            AuthLoginFlowRepository flowRepository,
            AuthSessionIssuer sessionIssuer,
            AccountRepository accountRepository,
            PasswordPolicy passwordPolicy,
            PasswordEncoder passwordEncoder
    ) {
        this.clock = clock;
        this.registrationTokenService = registrationTokenService;
        this.flowRepository = flowRepository;
        this.sessionIssuer = sessionIssuer;
        this.accountRepository = accountRepository;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @ObservedAction("auth.password_reset_complete")
    @AuditEvent(IdentityAuditEventEnum.PASSWORD_CHANGED)
    @Transactional
    public Result execute(Command command) {
        Instant now = clock.instant();
        AuthLoginFlow flow = registrationTokenService.consumeRegistrationToken(command.resetToken(), now);
        if (flow.getPurpose() != LoginFlowPurposeEnum.PASSWORD_RESET || flow.getAccountId() == null) {
            throw IdentityErrors.invalidRegistrationToken();
        }

        passwordPolicy.validate(command.newPassword());

        Account account = sessionIssuer.activeAccount(flow.getAccountId());
        if (account.hasPassword()
                && passwordEncoder.matches(command.newPassword(), account.getPasswordHash())) {
            throw IdentityErrors.passwordPolicyViolation(
                    Map.of("newPassword", "must differ from current password"));
        }
        account.setPassword(passwordEncoder.encode(command.newPassword()), now);
        accountRepository.save(account);

        flow.complete(account.getId(), now);
        flowRepository.save(flow);
        return new Result(account.getId());
    }
}
