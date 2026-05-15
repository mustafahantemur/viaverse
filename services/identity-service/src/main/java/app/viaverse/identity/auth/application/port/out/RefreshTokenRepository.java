package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.model.RefreshToken;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findActiveBySessionId(UUID sessionId);
}
