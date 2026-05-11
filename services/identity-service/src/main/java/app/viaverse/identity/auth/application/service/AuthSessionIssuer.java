package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.account.domain.AccountStatus;
import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.account.infrastructure.persistence.repository.IdentityAccountJpaRepository;
import app.viaverse.identity.auth.api.dto.AuthResponse;
import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import app.viaverse.identity.auth.domain.enums.RefreshTokenStatus;
import app.viaverse.identity.auth.domain.enums.SessionStatus;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthRefreshTokenJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthRefreshTokenJpaRepository;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthSessionJpaRepository;
import app.viaverse.identity.auth.infrastructure.security.JwtAccessTokenService;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuthSessionIssuer {
    private final AuthProperties properties;
    private final JwtAccessTokenService jwtAccessTokenService;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final IdentityAccountJpaRepository accountRepository;
    private final AuthSessionJpaRepository sessionRepository;
    private final AuthRefreshTokenJpaRepository refreshTokenRepository;

    public AuthSessionIssuer(
            AuthProperties properties,
            JwtAccessTokenService jwtAccessTokenService,
            SecureTokenGenerator tokenGenerator,
            TokenHasher tokenHasher,
            IdentityAccountJpaRepository accountRepository,
            AuthSessionJpaRepository sessionRepository,
            AuthRefreshTokenJpaRepository refreshTokenRepository
    ) {
        this.properties = properties;
        this.jwtAccessTokenService = jwtAccessTokenService;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuthResponse issue(IdentityAccountJpaEntity account, String userAgent, Instant now) {
        AuthSessionJpaEntity session = sessionRepository.save(new AuthSessionJpaEntity(
                UUID.randomUUID(),
                account.getId(),
                now,
                now.plus(properties.getRefreshTokenTtl()),
                normalizeOptional(userAgent)
        ));
        String refreshToken = tokenGenerator.generateUrlToken();
        refreshTokenRepository.save(new AuthRefreshTokenJpaEntity(
                UUID.randomUUID(),
                session.getId(),
                tokenHasher.hash(refreshToken),
                now,
                now.plus(properties.getRefreshTokenTtl())
        ));
        return new AuthResponse(
                AuthNextStep.AUTHENTICATED,
                jwtAccessTokenService.issue(account.getId(), session.getId(), now),
                refreshToken,
                jwtAccessTokenService.expiresInSeconds(),
                accountView(account)
        );
    }

    public AuthResponse issueForExistingSession(
            IdentityAccountJpaEntity account,
            AuthSessionJpaEntity session,
            String refreshToken,
            Instant now
    ) {
        return new AuthResponse(
                AuthNextStep.AUTHENTICATED,
                jwtAccessTokenService.issue(account.getId(), session.getId(), now),
                refreshToken,
                jwtAccessTokenService.expiresInSeconds(),
                accountView(account)
        );
    }

    public JwtPrincipal authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw IdentityErrors.bearerTokenRequired();
        }
        return jwtAccessTokenService.verify(authorizationHeader.substring("Bearer ".length()), Instant.now());
    }

    public AuthSessionJpaEntity activeSession(UUID sessionId, Instant now) {
        AuthSessionJpaEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(IdentityErrors::invalidSession);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw IdentityErrors.inactiveSession();
        }
        if (session.getExpiresAt().isBefore(now)) {
            session.expire(now);
            throw IdentityErrors.sessionExpired();
        }
        return session;
    }

    public IdentityAccountJpaEntity activeAccount(UUID accountId) {
        IdentityAccountJpaEntity account = accountRepository.findById(accountId)
                .orElseThrow(IdentityErrors::accountNotActive);
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw IdentityErrors.accountNotActive();
        }
        return account;
    }

    public AccountView accountView(IdentityAccountJpaEntity account) {
        return new AccountView(
                account.getId(),
                account.getStatus(),
                account.getDisplayName(),
                account.getFirstName(),
                account.getLastName(),
                account.isProfileCompleted(),
                account.getCreatedAt()
        );
    }

    public void revokeSession(AuthSessionJpaEntity session, Instant now) {
        session.revoke(now);
        for (AuthRefreshTokenJpaEntity token : refreshTokenRepository.findBySessionIdAndStatus(
                session.getId(),
                RefreshTokenStatus.ACTIVE
        )) {
            token.revoke(now);
        }
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
