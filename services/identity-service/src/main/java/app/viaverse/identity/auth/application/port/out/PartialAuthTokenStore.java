package app.viaverse.identity.auth.application.port.out;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Stores the short-lived opaque token issued after a successful primary
 * credential (password / social) when 2FA is required. The TOTP-verify
 * endpoint exchanges this token for a full session. Implementations should
 * apply server-side TTL so the token cannot be replayed forever.
 */
public interface PartialAuthTokenStore {
    void save(String tokenHash, UUID accountId, Duration ttl);

    Optional<UUID> findAccountId(String tokenHash);

    void delete(String tokenHash);
}
