package app.viaverse.identity.account.domain;

import java.time.Instant;
import java.util.UUID;

public record AccountView(
        UUID id,
        AccountStatusEnum status,
        String displayName,
        String firstName,
        String lastName,
        boolean profileCompleted,
        Instant createdAt
) {
}
