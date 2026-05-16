package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.account.application.port.out.AccountRepository;
import app.viaverse.identity.account.domain.AccountStatus;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.application.port.out.RefreshTokenRepository;
import app.viaverse.identity.auth.domain.enums.SessionStatus;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.domain.model.RefreshToken;
import app.viaverse.identity.auth.infrastructure.security.JwtAccessTokenService;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuthSessionIssuer {

    private final AuthProperties properties;
    private final JwtAccessTokenService jwtAccessTokenService;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final AccountRepository accountRepository;
    private final AuthSessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    public AuthSessionIssuer(
            AuthProperties properties,
            JwtAccessTokenService jwtAccessTokenService,
            SecureTokenGenerator tokenGenerator,
            TokenHasher tokenHasher,
            AccountRepository accountRepository,
            AuthSessionRepository sessionRepository,
            RefreshTokenRepository refreshTokenRepository,
            Clock clock
    ) {
        this.properties = properties;
        this.jwtAccessTokenService = jwtAccessTokenService;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.clock = clock;
    }

    public Issued issue(Account account, String userAgent, String clientIp, Instant now) {
        Instant refreshExpiresAt = now.plus(properties.getRefreshTokenTtl());
        AuthSession session = sessionRepository.save(AuthSession.issue(
                UUID.randomUUID(),
                account.getId(),
                refreshExpiresAt,
                normalizeOptional(userAgent),
                null,
                null,
                null,
                normalizeOptional(clientIp),
                now
        ));
        String refreshToken = tokenGenerator.generateUrlToken();
        refreshTokenRepository.save(RefreshToken.issue(
                UUID.randomUUID(),
                session.getId(),
                tokenHasher.hash(refreshToken),
                now,
                refreshExpiresAt
        ));
        String accessToken = jwtAccessTokenService.issue(account.getId(), session.getId(), now);
        Instant accessExpiresAt = now.plusSeconds(jwtAccessTokenService.expiresInSeconds());
        return new Issued(session, accessToken, accessExpiresAt, refreshToken, refreshExpiresAt);
    }

    public Issued issueForExistingSession(
            Account account,
            AuthSession session,
            String refreshToken,
            Instant refreshTokenExpiresAt,
            Instant now
    ) {
        String accessToken = jwtAccessTokenService.issue(account.getId(), session.getId(), now);
        Instant accessExpiresAt = now.plusSeconds(jwtAccessTokenService.expiresInSeconds());
        return new Issued(session, accessToken, accessExpiresAt, refreshToken, refreshTokenExpiresAt);
    }

    public AuthSession activeSession(UUID sessionId, Instant now) {
        AuthSession session = sessionRepository.findById(sessionId)
                .orElseThrow(IdentityErrors::invalidSession);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw IdentityErrors.inactiveSession();
        }
        if (session.getExpiresAt().isBefore(now)) {
            session.expire(now);
            sessionRepository.save(session);
            throw IdentityErrors.sessionExpired();
        }
        return session;
    }

    public Account activeAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(IdentityErrors::accountNotActive);
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw IdentityErrors.accountNotActive();
        }
        return account;
    }

    public void revokeSession(AuthSession session, Instant now) {
        session.revoke(now);
        sessionRepository.save(session);
        for (RefreshToken token : refreshTokenRepository.findActiveBySessionId(session.getId())) {
            token.revoke(now);
            refreshTokenRepository.save(token);
        }
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @SuppressWarnings("unused")
    private Instant now() {
        return clock.instant();
    }

    public record Issued(
            AuthSession session,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) {}
}
