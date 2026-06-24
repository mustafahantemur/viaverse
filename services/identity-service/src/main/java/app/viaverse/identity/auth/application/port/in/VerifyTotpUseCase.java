package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.shared.aspect.AuditableResult;
import java.time.Instant;
import java.util.UUID;

/**
 * Second step of password / social login when {@code two_factor_enabled} is
 * true. Consumes the partial-auth token issued by the first step plus the
 * 6-digit TOTP code from the user's authenticator app, and issues full
 * session tokens on success.
 */
public interface VerifyTotpUseCase {

    Result execute(Command command);

    record Command(String partialAuthToken, String totpCode, String userAgent, String clientIp) {}

    record Result(
            AuthNextStepEnum nextStep,
            UUID accountId,
            UUID sessionId,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) implements AuditableResult {}
}
