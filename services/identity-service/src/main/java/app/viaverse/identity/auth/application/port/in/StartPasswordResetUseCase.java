package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import java.time.Instant;
import java.util.UUID;

/**
 * First step of forgot-password. Opens a {@code PASSWORD_RESET} login flow
 * and dispatches an OTP to the supplied identifier — but only if the
 * identifier resolves to an active account. The response is intentionally
 * indistinguishable between "identifier exists" and "doesn't exist" so the
 * endpoint can't be used to enumerate accounts.
 */
public interface StartPasswordResetUseCase {

    Result execute(Command command);

    record Command(String identifier, String clientIp, String clientFingerprint) {}

    record Result(
            UUID flowId,
            IdentifierTypeEnum identifierType,
            Instant expiresAt,
            boolean dispatched
    ) {}
}
