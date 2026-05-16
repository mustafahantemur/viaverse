package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.RevokeSessionUseCase;
import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.application.port.out.SessionEventPublisher;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class RevokeSessionUseCaseImpl implements RevokeSessionUseCase {

    private final Clock clock;
    private final AuthSessionRepository sessionRepository;
    private final SessionEventPublisher sessionEventPublisher;

    public RevokeSessionUseCaseImpl(
            Clock clock,
            AuthSessionRepository sessionRepository,
            SessionEventPublisher sessionEventPublisher
    ) {
        this.clock = clock;
        this.sessionRepository = sessionRepository;
        this.sessionEventPublisher = sessionEventPublisher;
    }

    @Override
    @ObservedAction("sessions.revoke")
    public void execute(Command command) {
        Instant now = clock.instant();
        if (command.revokeAllExceptCurrent()) {
            for (AuthSession session : sessionRepository.findActiveByAccountId(command.accountId())) {
                if (session.getId().equals(command.currentSessionId())) {
                    continue;
                }
                session.revoke(now);
                sessionRepository.save(session);
                sessionEventPublisher.publishRevoked(session.getAccountId(), session.getId());
            }
            return;
        }
        AuthSession session = sessionRepository.findById(command.sessionId())
                .orElseThrow(IdentityErrors::invalidSession);
        if (!session.getAccountId().equals(command.accountId())) {
            throw IdentityErrors.invalidSession();
        }
        session.revoke(now);
        sessionRepository.save(session);
        sessionEventPublisher.publishRevoked(session.getAccountId(), session.getId());
    }
}
