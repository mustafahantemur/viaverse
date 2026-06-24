package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.model.AuthSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository {

    AuthSession save(AuthSession session);

    Optional<AuthSession> findById(UUID id);

    List<AuthSession> findActiveByAccountId(UUID accountId);
}
