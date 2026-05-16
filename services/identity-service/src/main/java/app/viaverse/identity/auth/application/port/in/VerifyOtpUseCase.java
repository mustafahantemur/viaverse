package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import java.time.Instant;
import java.util.UUID;

public interface VerifyOtpUseCase {

    Result execute(Command command);

    record Command(UUID flowId, String otp, String userAgent, String clientIp) {}

    record Result(
            AuthNextStepEnum nextStep,
            String registrationToken,
            Instant registrationExpiresAt,
            UUID accountId,
            UUID sessionId,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) {}
}
