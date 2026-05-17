package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.application.port.out.PartialAuthTokenStore;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Issues / consumes the opaque token that bridges a successful primary
 * credential and the TOTP step when 2FA is enabled. The plaintext token
 * never lives server-side; only its HMAC hash is stored in Valkey with a
 * 5-minute TTL.
 */
@Service
public class PartialAuthTokenService {
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final PartialAuthTokenStore store;

    public PartialAuthTokenService(
            SecureTokenGenerator tokenGenerator,
            TokenHasher tokenHasher,
            PartialAuthTokenStore store
    ) {
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
        this.store = store;
    }

    public Issued issue(UUID accountId, Instant now) {
        String token = tokenGenerator.generateUrlToken();
        store.save(tokenHasher.hash(token), accountId, DEFAULT_TTL);
        return new Issued(token, now.plus(DEFAULT_TTL));
    }

    public UUID consume(String token) {
        if (token == null || token.isBlank()) {
            throw IdentityErrors.invalidPartialAuthToken();
        }
        String hash = tokenHasher.hash(token);
        UUID accountId = store.findAccountId(hash)
                .orElseThrow(IdentityErrors::invalidPartialAuthToken);
        store.delete(hash);
        return accountId;
    }

    public record Issued(String token, Instant expiresAt) {}
}
