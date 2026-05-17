package app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import java.time.Instant;
import java.util.UUID;

public record TwoFactorEnrollmentResponse(
        UUID flowId,
        IdentifierTypeEnum otpIdentifierType,
        String otpIdentifierMasked,
        Instant otpExpiresAt,
        String secretBase32,
        String provisioningUri
) {
}
