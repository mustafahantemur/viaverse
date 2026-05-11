package app.viaverse.identity.application.auth;

import app.viaverse.identity.domain.auth.AuthNextStep;
import app.viaverse.identity.domain.auth.IdentifierType;
import java.time.Instant;
import java.util.UUID;

public record StartAuthResult(
        UUID flowId,
        IdentifierType identifierType,
        AuthNextStep nextStep,
        Instant expiresAt,
        String debugOtp
) {
}
