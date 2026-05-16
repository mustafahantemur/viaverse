package app.viaverse.identity.account.application.usecase;

import app.viaverse.identity.account.application.port.in.GetCurrentAccountUseCase;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.domain.enums.SessionStatus;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentAccountUseCaseImpl implements GetCurrentAccountUseCase {

    private final Clock clock;
    private final AuthSessionRepository sessionRepository;
    private final AccountRepository accountRepository;

    public GetCurrentAccountUseCaseImpl(
            Clock clock,
            AuthSessionRepository sessionRepository,
            AccountRepository accountRepository
    ) {
        this.clock = clock;
        this.sessionRepository = sessionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @ObservedAction("account.current")
    public Account execute(Command command) {
        Instant now = clock.instant();
        AuthSession session = sessionRepository.findById(command.sessionId())
                .orElseThrow(IdentityErrors::invalidSession);
        if (session.getStatus() != SessionStatus.ACTIVE || session.getExpiresAt().isBefore(now)) {
            throw IdentityErrors.invalidSession();
        }
        return accountRepository.findById(session.getAccountId())
                .orElseThrow(IdentityErrors::accountNotFound);
    }
}
