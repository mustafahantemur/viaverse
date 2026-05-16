package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import java.util.Optional;

public interface IdentifierRepository {

    IdentityIdentifier save(IdentityIdentifier identifier);

    Optional<IdentityIdentifier> findByTypeAndValue(IdentifierTypeEnum type, String normalizedValue);
}
