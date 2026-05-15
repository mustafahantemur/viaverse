package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.IdentifierType;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import java.util.Optional;

public interface IdentifierRepository {

    IdentityIdentifier save(IdentityIdentifier identifier);

    Optional<IdentityIdentifier> findByTypeAndValue(IdentifierType type, String normalizedValue);
}
