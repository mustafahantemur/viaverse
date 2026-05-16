package app.viaverse.identity.auth.api.dto.response;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import java.time.Instant;

public record AuthResponse(
        AuthNextStep nextStep,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        AccountView account
) implements VerifyOtpResponse {
}
