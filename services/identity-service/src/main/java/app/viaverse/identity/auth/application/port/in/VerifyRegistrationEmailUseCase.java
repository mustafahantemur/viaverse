package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.shared.aspect.AuditableResult;
import java.time.Instant;
import java.util.UUID;

/**
 * Stage 2 of the draft-based registration. Verifies the email OTP and:
 *
 * <ul>
 *   <li>If the draft has no phone number → creates the account
 *       immediately and returns full session tokens
 *       ({@link AuthNextStepEnum#AUTHENTICATED}).</li>
 *   <li>If the draft has a phone number → issues a phone OTP and
 *       returns {@code PHONE_VERIFICATION_REQUIRED} with the new
 *       {@code phoneFlowId} so the client can move to step 3.</li>
 * </ul>
 */
public interface VerifyRegistrationEmailUseCase {

    Result execute(Command command);

    record Command(UUID draftId, String otp, String clientIp, String userAgent) {}

    record Result(
            AuthNextStepEnum nextStep,
            UUID phoneFlowId,
            Instant phoneExpiresAt,
            UUID accountId,
            UUID sessionId,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) implements AuditableResult {}
}
