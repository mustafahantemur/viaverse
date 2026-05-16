package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.application.port.out.RefreshTokenRepository;
import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.domain.enums.RefreshTokenStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.domain.model.RefreshToken;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.error.RefreshTokenReuseDetectedException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenRotationService {

    private final AuthProperties properties;
    private final TokenHasher tokenHasher;
    private final SecureTokenGenerator tokenGenerator;
    private final AuthSessionIssuer sessionIssuer;
    private final AuthSessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenRotationService(
            AuthProperties properties,
            TokenHasher tokenHasher,
            SecureTokenGenerator tokenGenerator,
            AuthSessionIssuer sessionIssuer,
            AuthSessionRepository sessionRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.properties = properties;
        this.tokenHasher = tokenHasher;
        this.tokenGenerator = tokenGenerator;
        this.sessionIssuer = sessionIssuer;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Rotation rotate(String refreshToken, Instant now) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw IdentityErrors.refreshTokenRequired();
        }
        String hash = tokenHasher.hash(refreshToken);
        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(IdentityErrors::invalidRefreshToken);
        if (current.getStatus() != RefreshTokenStatusEnum.ACTIVE) {
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
        return new Rotation(current.getSessionId(), replacementRaw, replacement.getExpiresAt());
    }

    public RefreshToken revokeIfActive(String refreshToken, Instant now) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }
        return refreshTokenRepository.findByTokenHash(tokenHasher.hash(refreshToken))
                .filter(token -> token.getStatus() == RefreshTokenStatusEnum.ACTIVE)
                .map(token -> {
                    token.revoke(now);
                    return refreshTokenRepository.save(token);
                })
                .orElse(null);
    }

    private void handleReuse(RefreshToken token, Instant now) {
        AuthSession session = sessionRepository.findById(token.getSessionId())
                .orElseThrow(IdentityErrors::invalidRefreshToken);
        sessionIssuer.revokeSession(session, now);
        throw new RefreshTokenReuseDetectedException(session.getId(), session.getAccountId());
    }

    public record Rotation(UUID sessionId, String refreshToken, Instant refreshTokenExpiresAt) {
    }
}
