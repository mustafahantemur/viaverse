package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.domain.enums.RefreshTokenStatus;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthRefreshTokenJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthRefreshTokenJpaRepository;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthSessionJpaRepository;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.audit.IdentityAuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEvents;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.observability.audit.AuditLogger;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenRotationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenRotationService.class);

    private final AuthProperties properties;
    private final TokenHasher tokenHasher;
    private final SecureTokenGenerator tokenGenerator;
    private final AuthSessionJpaRepository sessionRepository;
    private final AuthRefreshTokenJpaRepository refreshTokenRepository;
    private final AuditLogger auditLogger;

    public RefreshTokenRotationService(
            AuthProperties properties,
            TokenHasher tokenHasher,
            SecureTokenGenerator tokenGenerator,
            AuthSessionJpaRepository sessionRepository,
            AuthRefreshTokenJpaRepository refreshTokenRepository,
            AuditLogger auditLogger
    ) {
        this.properties = properties;
        this.tokenHasher = tokenHasher;
        this.tokenGenerator = tokenGenerator;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditLogger = auditLogger;
    }

    public Rotation rotate(String refreshToken, Instant now) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw IdentityErrors.refreshTokenRequired();
        }
        AuthRefreshTokenJpaEntity currentToken = refreshTokenRepository
                .findByTokenHashAndStatus(tokenHasher.hash(refreshToken), RefreshTokenStatus.ACTIVE)
                .orElseGet(() -> handleRefreshTokenReuse(refreshToken, now));
        if (currentToken.getExpiresAt().isBefore(now)) {
            currentToken.expire(now);
            throw IdentityErrors.refreshTokenExpired();
        }

        String replacementRawToken = tokenGenerator.generateUrlToken();
        AuthRefreshTokenJpaEntity replacement = refreshTokenRepository.save(new AuthRefreshTokenJpaEntity(
                UUID.randomUUID(),
                currentToken.getSessionId(),
                tokenHasher.hash(replacementRawToken),
                now,
                now.plus(properties.getRefreshTokenTtl())
        ));
        currentToken.rotate(replacement.getId(), now);
        return new Rotation(currentToken.getSessionId(), replacementRawToken);
    }

    public AuthRefreshTokenJpaEntity activeRefreshTokenOrNull(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }
        return refreshTokenRepository
                .findByTokenHashAndStatus(tokenHasher.hash(refreshToken), RefreshTokenStatus.ACTIVE)
                .orElse(null);
    }

    private AuthRefreshTokenJpaEntity handleRefreshTokenReuse(String refreshToken, Instant now) {
        AuthRefreshTokenJpaEntity token = refreshTokenRepository.findByTokenHash(tokenHasher.hash(refreshToken))
                .orElseThrow(IdentityErrors::invalidRefreshToken);
        if (token.getStatus() == RefreshTokenStatus.ROTATED || token.getStatus() == RefreshTokenStatus.REVOKED) {
            LOGGER.atWarn()
                    .addKeyValue("event.action", "token.refresh")
                    .addKeyValue("event.outcome", "reuse_detected")
                    .addKeyValue("auth.session_id", token.getSessionId())
                    .log("token.refresh reuse_detected");
            sessionRepository.findById(token.getSessionId()).ifPresent(session -> {
                IdentityAuditEvents.recordAccountSecurityEvent(
                        auditLogger,
                        session.getAccountId(),
                        IdentityAuditEvent.REFRESH_TOKEN_REUSED
                );
                revokeSessionTokens(session, now);
            });
        }
        throw IdentityErrors.invalidRefreshToken();
    }

    private void revokeSessionTokens(AuthSessionJpaEntity session, Instant now) {
        session.revoke(now);
        for (AuthRefreshTokenJpaEntity activeToken : refreshTokenRepository.findBySessionIdAndStatus(
                session.getId(),
                RefreshTokenStatus.ACTIVE
        )) {
            activeToken.revoke(now);
        }
    }

    public record Rotation(UUID sessionId, String refreshToken) {
    }
}
