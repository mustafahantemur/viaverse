package app.viaverse.identity.auth.application.port.in;

import java.time.Instant;
import java.util.UUID;

/**
 * Second step of forgot-password. Validates the OTP issued by
 * {@code StartPasswordResetUseCase} and hands back a reset token the user
 * exchanges in step 3 for a new password.
 */
public interface VerifyPasswordResetOtpUseCase {

    Result execute(Command command);

    record Command(UUID flowId, String otp, String clientIp) {}

    record Result(String resetToken, Instant expiresAt) {}
}
