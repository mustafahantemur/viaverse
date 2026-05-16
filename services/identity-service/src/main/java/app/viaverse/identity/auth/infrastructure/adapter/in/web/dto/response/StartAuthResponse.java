package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import java.time.Instant;
import java.util.UUID;

public record StartAuthResponse(
        UUID flowId,
        IdentifierTypeEnum identifierType,
        AuthNextStepEnum nextStep,
        Instant expiresAt,
        String debugOtp
) {
}
