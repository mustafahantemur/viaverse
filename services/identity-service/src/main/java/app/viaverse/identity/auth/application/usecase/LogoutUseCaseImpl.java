package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.LogoutUseCase;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RefreshTokenRotationService;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.domain.model.RefreshToken;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final Clock clock;
    private final RefreshTokenRotationService rotationService;
    private final AuthSessionIssuer sessionIssuer;

    public LogoutUseCaseImpl(
            Clock clock,
            RefreshTokenRotationService rotationService,
            AuthSessionIssuer sessionIssuer
    ) {
        this.clock = clock;
        this.rotationService = rotationService;
        this.sessionIssuer = sessionIssuer;
    }

    @Override
    @ObservedAction("auth.logout")
    @AuditEvent(IdentityAuditEventEnum.LOGOUT)
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
        AuthSession session = sessionIssuer.activeSession(sessionId, now);
        sessionIssuer.revokeSession(session, now);
    }
}

