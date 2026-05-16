package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.application.port.out.RefreshTokenRepository;
import app.viaverse.identity.auth.application.port.out.SessionEventPublisher;
import app.viaverse.identity.auth.domain.enums.RefreshTokenStatus;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.domain.model.RefreshToken;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.aspect.RefreshTokenReuseDetectedException;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenRotationService {

    private final AuthProperties properties;
    private final TokenHasher tokenHasher;
    private final SecureTokenGenerator tokenGenerator;
    private final AuthSessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SessionEventPublisher sessionEventPublisher;

    public RefreshTokenRotationService(
            AuthProperties properties,
            TokenHasher tokenHasher,
            SecureTokenGenerator tokenGenerator,
            AuthSessionRepository sessionRepository,
            RefreshTokenRepository refreshTokenRepository,
            SessionEventPublisher sessionEventPublisher
    ) {
        this.properties = properties;
        this.tokenHasher = tokenHasher;
        this.tokenGenerator = tokenGenerator;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.sessionEventPublisher = sessionEventPublisher;
    }

    public Rotation rotate(String refreshToken, Instant now) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw IdentityErrors.refreshTokenRequired();
        }
        String hash = tokenHasher.hash(refreshToken);
        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(IdentityErrors::invalidRefreshToken);
        if (current.getStatus() != RefreshTokenStatus.ACTIVE) {
            handleReuse(current, now);
        }
        if (current.getExpiresAt().isBefore(now)) {
            current.expire(now);
            refreshTokenRepository.save(current);
            throw IdentityErrors.refreshTokenExpired();
        }

        String replacementRaw = tokenGenerator.generateUrlToken();
        RefreshToken replacement = refreshTokenRepository.save(RefreshToken.issue(
                UUID.randomUUID(),
                current.getSessionId(),
                tokenHasher.hash(replacementRaw),
                now,
                now.plus(properties.getRefreshTokenTtl())
        ));
        current.rotate(replacement.getId(), now);
        refreshTokenRepository.save(current);
        return new Rotation(current.getSessionId(), replacementRaw);
    }

    public RefreshToken revokeIfActive(String refreshToken, Instant now) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }
        return refreshTokenRepository.findByTokenHash(tokenHasher.hash(refreshToken))
                .filter(token -> token.getStatus() == RefreshTokenStatus.ACTIVE)
                .map(token -> {
                    token.revoke(now);
                    return refreshTokenRepository.save(token);
                })
                .orElse(null);
    }

    private void handleReuse(RefreshToken token, Instant now) {
        AuthSession session = sessionRepository.findById(token.getSessionId())
                .orElseThrow(IdentityErrors::invalidRefreshToken);
        session.revoke(now);
        sessionRepository.save(session);
        for (RefreshToken active : refreshTokenRepository.findActiveBySessionId(session.getId())) {
            active.revoke(now);
            refreshTokenRepository.save(active);
        }
        sessionEventPublisher.publishRevoked(session.getAccountId(), session.getId());
        throw new RefreshTokenReuseDetectedException(session.getId(), session.getAccountId());
    }

    public record Rotation(UUID sessionId, String refreshToken) {
    }
}
