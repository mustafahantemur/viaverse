package app.viaverse.identity.auth.api.dto;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.auth.domain.enums.AuthNextStep;

public record AuthResponse(
        AuthNextStep nextStep,
        String accessToken,
        String refreshToken,
        long expiresIn,
        AccountView account
) implements VerifyOtpResponse {
}
