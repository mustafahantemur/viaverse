package app.viaverse.identity.auth.api.dto;

import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import java.time.Instant;

public record RegistrationRequiredResponse(
        AuthNextStep nextStep,
        String registrationToken,
        Instant registrationExpiresAt
) implements VerifyOtpResponse {
}
