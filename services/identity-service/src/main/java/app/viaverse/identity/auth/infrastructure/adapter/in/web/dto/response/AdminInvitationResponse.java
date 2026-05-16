package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import java.time.Instant;

public record AdminInvitationResponse(
        String invitationToken,
        Instant expiresAt
) {
}
