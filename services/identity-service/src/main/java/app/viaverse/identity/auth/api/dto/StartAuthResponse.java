package app.viaverse.identity.auth.api.dto;

import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import app.viaverse.identity.auth.domain.enums.IdentifierType;
import java.time.Instant;
import java.util.UUID;

public record StartAuthResponse(
        UUID flowId,
        IdentifierType identifierType,
        AuthNextStep nextStep,
        Instant expiresAt,
        String debugOtp
) {
}
