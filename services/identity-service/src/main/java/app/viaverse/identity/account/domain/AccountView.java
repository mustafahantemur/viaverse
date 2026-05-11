package app.viaverse.identity.account.domain;

import java.time.Instant;
import java.util.UUID;

public record AccountView(
        UUID id,
        AccountStatus status,
        String displayName,
        String firstName,
        String lastName,
        boolean profileCompleted,
        Instant createdAt
) {
}
