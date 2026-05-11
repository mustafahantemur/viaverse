package app.viaverse.identity.application.auth;

import java.time.Instant;
import java.util.UUID;

public record OtpDeliveryRequest(
        UUID flowId,
        NormalizedIdentifier identifier,
        String otp,
        Instant expiresAt
) {
}
