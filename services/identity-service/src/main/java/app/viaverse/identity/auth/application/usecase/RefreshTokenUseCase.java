package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.auth.api.dto.AuthResponse;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RefreshTokenRotationService;
import app.viaverse.identity.auth.application.service.RefreshTokenRotationService.Rotation;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.shared.audit.IdentityAuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEvents;
import app.viaverse.identity.shared.error.IdentityException;
import app.viaverse.identity.shared.logging.ActionLogContext;
import app.viaverse.identity.shared.logging.ObservedAction;
import app.viaverse.observability.audit.AuditLogger;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenUseCase {
    private final RefreshTokenRotationService rotationService;
    private final AuthSessionIssuer sessionIssuer;
    private final AuditLogger auditLogger;

    public RefreshTokenUseCase(
            RefreshTokenRotationService rotationService,
            AuthSessionIssuer sessionIssuer,
            AuditLogger auditLogger
    ) {
        this.rotationService = rotationService;
        this.sessionIssuer = sessionIssuer;
        this.auditLogger = auditLogger;
    }

    @ObservedAction("token.refresh")
    @Transactional(noRollbackFor = IdentityException.class)
    public AuthResponse refresh(String refreshToken, String userAgent) {
        Instant now = Instant.now();
        Rotation rotation = rotationService.rotate(refreshToken, now);
        AuthSessionJpaEntity session = sessionIssuer.activeSession(rotation.sessionId(), now);
        ActionLogContext.put("auth.session_id", session.getId());
        session.touch(now);
        IdentityAccountJpaEntity account = sessionIssuer.activeAccount(session.getAccountId());
        IdentityAuditEvents.recordAccountSecurityEvent(auditLogger, account.getId(), IdentityAuditEvent.REFRESH_TOKEN_ROTATED);
        ActionLogContext.put("user.id", account.getId());
        return sessionIssuer.issueForExistingSession(account, session, rotation.refreshToken(), now);
    }
}
