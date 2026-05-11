package app.viaverse.identity.application.auth;

import app.viaverse.identity.domain.auth.AuthNextStep;

public record AuthResult(
        AuthNextStep nextStep,
        String accessToken,
        String refreshToken,
        long expiresIn,
        AccountView account
) {
}
