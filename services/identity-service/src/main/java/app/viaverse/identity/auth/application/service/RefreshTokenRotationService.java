package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.domain.enums.RefreshTokenStatus;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthRefreshTokenJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthRefreshTokenJpaRepository;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthSessionJpaRepository;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenRotationService {
    private final AuthProperties properties;
    private final TokenHasher tokenHasher;
    private final SecureTokenGenerator tokenGenerator;
    private final AuthSessionJpaRepository sessionRepository;
    private final AuthRefreshTokenJpaRepository refreshTokenRepository;

    public RefreshTokenRotationService(
            AuthProperties properties,
            TokenHasher tokenHasher,
            SecureTokenGenerator tokenGenerator,
            AuthSessionJpaRepository sessionRepository,
            AuthRefreshTokenJpaRepository refreshTokenRepository
    ) {
        this.properties = properties;
        this.tokenHasher = tokenHasher;
        this.tokenGenerator = tokenGenerator;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
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
            sessionRepository.findById(token.getSessionId()).ifPresent(session -> revokeSessionTokens(session, now));
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
