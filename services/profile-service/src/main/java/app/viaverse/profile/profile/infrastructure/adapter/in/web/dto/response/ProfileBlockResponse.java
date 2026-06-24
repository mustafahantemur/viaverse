package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response;

import java.util.UUID;

public record ProfileBlockResponse(
        UUID blockedAccountId,
        String reason
) {
}
