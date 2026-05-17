package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.ChangePasswordUseCase;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.domain.policy.PasswordPolicy;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final Clock clock;
    private final AuthSessionIssuer sessionIssuer;
    private final AccountRepository accountRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCaseImpl(
            Clock clock,
            AuthSessionIssuer sessionIssuer,
            AccountRepository accountRepository,
            PasswordPolicy passwordPolicy,
            PasswordEncoder passwordEncoder
    ) {
        this.clock = clock;
        this.sessionIssuer = sessionIssuer;
        this.accountRepository = accountRepository;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @ObservedAction("auth.password_change")
    @AuditEvent(IdentityAuditEventEnum.PASSWORD_CHANGED)
    @Transactional
    public void execute(Command command) {
        Instant now = clock.instant();
        Account account = sessionIssuer.activeAccount(command.accountId());

        // If the account already has a password, the caller must prove they
        // know it. Social-only accounts (no password set yet) may set their
        // first password without providing a current one.
        if (account.hasPassword()) {
            if (command.currentPassword() == null || command.currentPassword().isBlank()) {
                throw IdentityErrors.invalidCredentials();
            }
            if (!passwordEncoder.matches(command.currentPassword(), account.getPasswordHash())) {
                throw IdentityErrors.invalidCredentials();
            }
        }

        passwordPolicy.validate(command.newPassword());
        // Reject no-op rotations to nudge users toward an actually new secret.
        if (account.hasPassword()
                && passwordEncoder.matches(command.newPassword(), account.getPasswordHash())) {
            throw IdentityErrors.passwordPolicyViolation(
                    java.util.Map.of("newPassword", "must differ from current password"));
        }

        account.setPassword(passwordEncoder.encode(command.newPassword()), now);
        accountRepository.save(account);
    }
}
