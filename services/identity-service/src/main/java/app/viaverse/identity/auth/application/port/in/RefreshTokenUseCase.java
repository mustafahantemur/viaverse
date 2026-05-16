package app.viaverse.identity.auth.application.port.in;

import java.time.Instant;
import java.util.UUID;
import app.viaverse.identity.shared.aspect.AuditableResult;

public interface RefreshTokenUseCase {

    Result execute(Command command);

    record Command(String refreshToken, String userAgent, String clientIp) {}

    record Result(
            UUID accountId,
            UUID sessionId,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) implements AuditableResult {}
}
