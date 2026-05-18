package app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response;

import java.util.UUID;

public record ProviderReadinessResponse(
        UUID accountId,
        boolean active,
        boolean hasVerifiedIdentifier
) {
}
