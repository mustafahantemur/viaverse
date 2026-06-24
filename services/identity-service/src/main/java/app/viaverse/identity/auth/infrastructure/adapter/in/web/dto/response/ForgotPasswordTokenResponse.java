package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import java.time.Instant;

public record ForgotPasswordTokenResponse(
        String resetToken,
        Instant expiresAt
) {
}
