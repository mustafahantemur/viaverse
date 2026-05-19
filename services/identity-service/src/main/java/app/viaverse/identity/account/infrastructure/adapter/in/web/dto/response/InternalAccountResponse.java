package app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response;

import java.time.Instant;
import java.util.UUID;

public record InternalAccountResponse(
        UUID accountId,
        String displayName,
        String firstName,
        String lastName,
        Instant createdAt
) {
}
