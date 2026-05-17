package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import java.time.Instant;

/**
 * Returned by password-login or social-signin when the account has 2FA
 * enabled. The client should immediately POST {@code /auth/verify-totp}
 * with {@code partialAuthToken} + the 6-digit code from the authenticator.
 */
public record TotpRequiredResponse(
        AuthNextStepEnum nextStep,
        String partialAuthToken,
        Instant partialAuthExpiresAt
) implements AuthCompletionResponse {
}
