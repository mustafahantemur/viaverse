package app.viaverse.identity.application.auth;

import app.viaverse.identity.domain.auth.AuthNextStep;
import java.time.Instant;

public record RegistrationRequiredResult(AuthNextStep nextStep, String registrationToken, Instant expiresAt) {
}
