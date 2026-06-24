package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import java.time.Instant;
import java.util.UUID;

/**
 * Response is the same shape whether the identifier exists or not — clients
 * can't distinguish, which neutralises the endpoint as an enumeration vector.
 */
public record ForgotPasswordStartResponse(
        UUID flowId,
        IdentifierTypeEnum identifierType,
        Instant expiresAt
) {
}
