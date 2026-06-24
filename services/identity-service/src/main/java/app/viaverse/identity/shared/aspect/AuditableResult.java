package app.viaverse.identity.shared.aspect;

import java.util.UUID;

/**
 * Implemented by use-case Result records that should emit an audit event
 * upon successful completion. Provides the account id used as the audit
 * actor/resource identifier.
 */
public interface AuditableResult {
    UUID accountId();
}
