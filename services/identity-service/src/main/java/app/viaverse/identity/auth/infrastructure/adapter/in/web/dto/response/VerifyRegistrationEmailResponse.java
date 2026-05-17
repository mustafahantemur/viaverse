package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import java.time.Instant;
import java.util.UUID;

/**
 * Email verification result. The shape carries either the next step (phone
 * verification pending) <em>or</em> the full session — the {@code nextStep}
 * field disambiguates.
 */
public record VerifyRegistrationEmailResponse(
        AuthNextStepEnum nextStep,
        UUID phoneFlowId,
        Instant phoneExpiresAt,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        AccountView account
) {
}
