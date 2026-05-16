package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import java.time.Instant;
import java.util.UUID;

public interface SocialSignInUseCase {

    Result execute(Command command);

    record Command(
            SocialAuthProviderEnum provider,
            String idToken,
            String nonce,
            String userAgent,
            String clientIp,
            String clientFingerprint
    ) {
    }

    record Result(
            AuthNextStepEnum nextStep,
            String registrationToken,
            Instant registrationExpiresAt,
            UUID accountId,
            UUID sessionId,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) {
    }
}
