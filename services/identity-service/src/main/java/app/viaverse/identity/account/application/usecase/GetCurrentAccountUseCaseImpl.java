package app.viaverse.identity.account.application.usecase;

import app.viaverse.identity.account.application.port.in.GetCurrentAccountUseCase;
import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.application.port.out.SessionCachePort;
import app.viaverse.identity.auth.domain.enums.SessionStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentAccountUseCaseImpl implements GetCurrentAccountUseCase {

    private final Clock clock;
    private final AuthSessionRepository sessionRepository;
    private final SessionCachePort sessionCachePort;
    private final AccountRepository accountRepository;

    public GetCurrentAccountUseCaseImpl(
            Clock clock,
            AuthSessionRepository sessionRepository,
            SessionCachePort sessionCachePort,
            AccountRepository accountRepository
    ) {
        this.clock = clock;
        this.sessionRepository = sessionRepository;
        this.sessionCachePort = sessionCachePort;
        this.accountRepository = accountRepository;
    }

    @Override
    @ObservedAction("account.current")
    public Account execute(Command command) {
        Instant now = clock.instant();
        var cachedSession = sessionCachePort.find(command.sessionId());
        if (cachedSession.isPresent()) {
            SessionCachePort.Snapshot snapshot = cachedSession.get();
            if (snapshot.status() != SessionStatusEnum.ACTIVE) {
                throw IdentityErrors.invalidSession();
            }
            if (snapshot.expiresAt().isAfter(now)) {
                return accountRepository.findById(snapshot.accountId())
                        .orElseThrow(IdentityErrors::accountNotActive);
            }
            sessionCachePort.evict(command.sessionId());
        }
        AuthSession session = sessionRepository.findById(command.sessionId())
                .orElseThrow(IdentityErrors::invalidSession);
        if (session.getStatus() != SessionStatusEnum.ACTIVE || !session.getExpiresAt().isAfter(now)) {
            throw IdentityErrors.invalidSession();
        }
        sessionCachePort.put(session, now);
        return accountRepository.findById(session.getAccountId())
                .orElseThrow(IdentityErrors::accountNotActive);
    }
}

