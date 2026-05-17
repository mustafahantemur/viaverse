package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import java.time.Instant;
import java.util.UUID;

/**
 * First step of TOTP enrollment for an already authenticated account.
 * Generates a fresh secret and stashes it in Valkey as "pending", then
 * issues an OTP to the account's primary verified identifier to prove
 * the user still controls it. The user scans the {@code provisioningUri}
 * into their authenticator app, then submits both the OTP and the first
 * 6-digit TOTP code to {@code /me/2fa/confirm} to activate.
 */
public interface StartTwoFactorEnrollmentUseCase {

    Result execute(Command command);

    record Command(UUID accountId, String clientIp) {}

    record Result(
            UUID flowId,
            IdentifierTypeEnum otpIdentifierType,
            String otpIdentifierMasked,
            Instant otpExpiresAt,
            String secretBase32,
            String provisioningUri
    ) {}
}
