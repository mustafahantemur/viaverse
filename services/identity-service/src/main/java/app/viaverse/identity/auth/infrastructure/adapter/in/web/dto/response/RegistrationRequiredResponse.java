package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import java.time.Instant;

public record RegistrationRequiredResponse(
        AuthNextStepEnum nextStep,
        String registrationToken,
        Instant registrationExpiresAt
) implements VerifyOtpResponse {
}
