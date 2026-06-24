package app.viaverse.identity.shared.aspect;

import java.util.UUID;

/**
 * Implemented by use-case Command records whose audit subject must come from
 * the input rather than the result (e.g. {@code LogoutUseCase.Command},
 * {@code RevokeSessionUseCase.Command} — both methods return {@code void}).
 *
 * <p>The aspect resolves the account id in this order:
 * <ol>
 *   <li>{@link AuditableResult#accountId()} on the return value</li>
 *   <li>{@code AuditableCommand#accountId()} on any input argument</li>
 *   <li>{@code @LogParam("user.id") UUID} method parameter</li>
 * </ol>
 */
public interface AuditableCommand {
    UUID accountId();
}
