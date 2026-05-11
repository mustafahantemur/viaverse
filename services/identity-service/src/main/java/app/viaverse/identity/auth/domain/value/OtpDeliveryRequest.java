package app.viaverse.identity.auth.domain.value;

import java.time.Instant;
import java.util.UUID;

public record OtpDeliveryRequest(
        UUID flowId,
        NormalizedIdentifier identifier,
        String otp,
        Instant expiresAt
) {
}
