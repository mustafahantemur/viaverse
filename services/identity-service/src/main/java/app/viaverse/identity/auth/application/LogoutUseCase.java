package app.viaverse.identity.auth.application;

import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RefreshTokenRotationService;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthRefreshTokenJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthSessionJpaRepository;
import app.viaverse.identity.shared.audit.IdentityAuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEvents;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.observability.audit.AuditLogger;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutUseCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutUseCase.class);

    private final AuthSessionIssuer sessionIssuer;
    private final RefreshTokenRotationService rotationService;
    private final AuthSessionJpaRepository sessionRepository;
    private final AuditLogger auditLogger;

    public LogoutUseCase(
            AuthSessionIssuer sessionIssuer,
            RefreshTokenRotationService rotationService,
            AuthSessionJpaRepository sessionRepository,
            AuditLogger auditLogger
    ) {
        this.sessionIssuer = sessionIssuer;
        this.rotationService = rotationService;
        this.sessionRepository = sessionRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional
    public void logout(String authorizationHeader, String refreshToken) {
        Instant now = Instant.now();
        UUID sessionId = null;
        AuthRefreshTokenJpaEntity token = rotationService.activeRefreshTokenOrNull(refreshToken);
        if (token != null) {
            token.revoke(now);
            sessionId = token.getSessionId();
        }
        if (sessionId == null) {
            sessionId = sessionIssuer.authenticate(authorizationHeader).sessionId();
        }
        AuthSessionJpaEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(IdentityErrors::invalidSession);
        sessionIssuer.revokeSession(session, now);
        IdentityAuditEvents.recordAccountSecurityEvent(auditLogger, session.getAccountId(), IdentityAuditEvent.LOGOUT);
        LOGGER.atInfo()
                .addKeyValue("event.action", "auth.logout")
                .addKeyValue("event.outcome", "success")
                .addKeyValue("auth.session_id", session.getId())
                .addKeyValue("user.id", session.getAccountId())
                .log("auth.logout succeeded");
    }
}
