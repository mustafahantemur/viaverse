package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import java.time.Instant;

/**
 * Verifies the OTP issued by {@code POST /auth/start} for a brand-new
 * identifier. Returns a short-lived registration token that
 * {@code POST /auth/register/complete} consumes to actually create the
 * account (with display name, password, consents).
 *
 * <p>Only flows with purpose {@code REGISTRATION} are accepted here — OTPs
 * issued for 2FA enrollment, identifier verification, or password reset
 * have their own dedicated endpoints under {@code /me/*}.
 *
 * <p>Result does not implement {@code AuditableResult} because no account
 * exists yet at this point in the flow — {@code OTP_VERIFIED} audit is
 * intentionally emitted without an accountId.
 */
public interface VerifyOtpUseCase {

    Result execute(Command command);

    record Command(java.util.UUID flowId, String otp, String clientIp) {}

    record Result(
            AuthNextStepEnum nextStep,
            String registrationToken,
            Instant registrationExpiresAt
    ) {}
}
