package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.shared.aspect.AuditableResult;
import java.time.Instant;
import java.util.UUID;

/**
 * Verifies identifier + password. On success, either returns full session
 * tokens ({@link AuthNextStepEnum#AUTHENTICATED}) or a short-lived
 * {@code partialAuthToken} plus {@link AuthNextStepEnum#TOTP_REQUIRED}
 * that the client exchanges via {@code POST /auth/verify-totp}.
 *
 * <p>All failure paths — unknown identifier, wrong password, social-only
 * account with no password set — collapse to a single
 * {@code AUTH_INVALID_CREDENTIALS} error so an attacker cannot enumerate
 * which identifiers exist via timing or error code differences.
 */
public interface PasswordLoginUseCase {

    Result execute(Command command);

    record Command(String identifier, String password, String userAgent, String clientIp) {}

    record Result(
            AuthNextStepEnum nextStep,
            UUID accountId,
            UUID sessionId,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt,
            String partialAuthToken,
            Instant partialAuthExpiresAt
    ) implements AuditableResult {}
}
