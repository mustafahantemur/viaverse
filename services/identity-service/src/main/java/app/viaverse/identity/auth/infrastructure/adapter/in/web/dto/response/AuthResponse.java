package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import java.time.Instant;

public record AuthResponse(
        AuthNextStepEnum nextStep,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        AccountView account
) implements VerifyOtpResponse {
}
