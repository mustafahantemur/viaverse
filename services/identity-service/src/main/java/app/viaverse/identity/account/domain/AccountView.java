package app.viaverse.identity.account.domain;

import app.viaverse.identity.account.domain.AccountStatus;
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
