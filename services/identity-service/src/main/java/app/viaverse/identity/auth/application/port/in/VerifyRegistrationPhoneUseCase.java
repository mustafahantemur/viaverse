package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.shared.aspect.AuditableResult;
import java.time.Instant;
import java.util.UUID;

/**
 * Stage 3 (only when the draft has a phone): verifies the phone OTP and
 * creates the account with both identifiers verified. Returns full
 * session tokens.
 */
public interface VerifyRegistrationPhoneUseCase {

    Result execute(Command command);

    record Command(UUID draftId, String otp, String clientIp, String userAgent) {}

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
