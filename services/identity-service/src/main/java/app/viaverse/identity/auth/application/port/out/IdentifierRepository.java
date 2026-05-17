package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentifierRepository {

    IdentityIdentifier save(IdentityIdentifier identifier);

    Optional<IdentityIdentifier> findByTypeAndValue(IdentifierTypeEnum type, String normalizedValue);

    /**
     * All verified identifiers attached to an account, in stable order. Used
     * to pick which identifier should receive proof-of-ownership OTPs for 2FA
     * enroll/disable: callers typically prefer the first EMAIL identifier and
     * fall back to PHONE.
     */
    List<IdentityIdentifier> findByAccountId(UUID accountId);
}
