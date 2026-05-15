package app.viaverse.identity.auth.domain.model;

import app.viaverse.identity.auth.domain.enums.IdentifierType;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing a verified identifier (phone, email, social) bound to an account.
 * <p>
 * Immutable Java record — identifiers are append-only; mutation happens by issuing a new record.
 * No JPA / Spring annotations.
 */
public record IdentityIdentifier(
        UUID id,
        UUID accountId,
        IdentifierType identifierType,
        String normalizedIdentifier,
        Instant verifiedAt,
        Instant createdAt
) {
    public IdentityIdentifier {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(identifierType, "identifierType");
        Objects.requireNonNull(normalizedIdentifier, "normalizedIdentifier");
        Objects.requireNonNull(verifiedAt, "verifiedAt");
        Objects.requireNonNull(createdAt, "createdAt");
    }

    /**
     * Factory for a freshly-verified identifier binding.
     */
    public static IdentityIdentifier verify(
            UUID id,
            UUID accountId,
            IdentifierType identifierType,
            String normalizedIdentifier,
            Instant now
    ) {
        return new IdentityIdentifier(id, accountId, identifierType, normalizedIdentifier, now, now);
    }
}
