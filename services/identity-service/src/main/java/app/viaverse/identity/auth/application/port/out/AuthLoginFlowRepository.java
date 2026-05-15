package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import java.util.Optional;
import java.util.UUID;

public interface AuthLoginFlowRepository {

    AuthLoginFlow save(AuthLoginFlow flow);

    Optional<AuthLoginFlow> findById(UUID id);

    Optional<AuthLoginFlow> findByRegistrationTokenHash(String registrationTokenHash);
}
