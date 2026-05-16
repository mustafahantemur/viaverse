package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.SessionStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthSession;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionCachePort {

    void put(AuthSession session, Instant now);

    Optional<Snapshot> find(UUID sessionId);

    void evict(UUID sessionId);

    record Snapshot(UUID accountId, SessionStatusEnum status, Instant expiresAt) {
    }
}
