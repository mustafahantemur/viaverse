package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.LogoutUseCase;
import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.application.port.out.SessionEventPublisher;
import app.viaverse.identity.auth.application.service.RefreshTokenRotationService;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.domain.model.RefreshToken;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final Clock clock;
    private final RefreshTokenRotationService rotationService;
    private final AuthSessionRepository sessionRepository;
    private final SessionEventPublisher sessionEventPublisher;

    public LogoutUseCaseImpl(
            Clock clock,
            RefreshTokenRotationService rotationService,
            AuthSessionRepository sessionRepository,
            SessionEventPublisher sessionEventPublisher
    ) {
        this.clock = clock;
        this.rotationService = rotationService;
        this.sessionRepository = sessionRepository;
        this.sessionEventPublisher = sessionEventPublisher;
    }

    @Override
    @ObservedAction("auth.logout")
    public void execute(Command command) {
        Instant now = clock.instant();
        UUID sessionId = null;
        if (command.refreshToken() != null) {
            RefreshToken token = rotationService.revokeIfActive(command.refreshToken(), now);
            if (token != null) {
                sessionId = token.getSessionId();
            }
        }
        if (sessionId == null) {
            if (command.principalSessionId() == null) {
                throw IdentityErrors.bearerTokenRequired();
            }
            sessionId = command.principalSessionId();
        }
        AuthSession session = sessionRepository.findById(sessionId)
                .orElseThrow(IdentityErrors::invalidSession);
        session.revoke(now);
        sessionRepository.save(session);
        sessionEventPublisher.publishRevoked(session.getAccountId(), session.getId());
    }
}
