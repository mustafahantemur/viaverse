package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.model.RegistrationDraft;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Cache port for in-flight registration drafts. Implementations should
 * apply a server-side TTL and persist the draft as opaque bytes — the
 * password is already hashed before it gets here, but the rest of the
 * form data is still PII and should be wiped after expiry.
 */
public interface RegistrationDraftStore {

    void save(RegistrationDraft draft, Duration ttl);

    Optional<RegistrationDraft> findById(UUID draftId);

    void delete(UUID draftId);
}
