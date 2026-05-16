package app.viaverse.identity.shared.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a use-case method that should emit an audit log entry when it
 * completes successfully. The aspect resolves the account id either from
 * the returned {@link AuditableResult} or from a parameter annotated with
 * {@code @LogParam("user.id")}.
 *
 * Note: this annotation is intentionally named {@code AuditEvent}; the
 * underlying record type in {@code app.viaverse.observability.audit} is
 * referenced by fully-qualified name where both are needed.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditEvent {
    IdentityAuditEvent value();
}
