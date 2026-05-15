package app.viaverse.identity.auth.application.port.out;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationTokenStore {

    void save(String tokenHash, UUID flowId, Duration ttl);

    Optional<UUID> findFlowId(String tokenHash);

    void delete(String tokenHash);
}
