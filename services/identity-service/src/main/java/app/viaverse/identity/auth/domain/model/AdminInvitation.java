package app.viaverse.identity.auth.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class AdminInvitation {
    private final UUID id;
    private final String tokenHash;
    private final UUID issuedByAccountId;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Instant consumedAt;
    private Instant updatedAt;

    public AdminInvitation(
            UUID id,
            String tokenHash,
            UUID issuedByAccountId,
            Instant expiresAt,
            Instant consumedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        this.issuedByAccountId = Objects.requireNonNull(issuedByAccountId, "issuedByAccountId");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.consumedAt = consumedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static AdminInvitation issue(
            UUID id,
            String tokenHash,
            UUID issuedByAccountId,
            Instant expiresAt,
            Instant now
    ) {
        return new AdminInvitation(id, tokenHash, issuedByAccountId, expiresAt, null, now, now);
    }

    public UUID getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public UUID getIssuedByAccountId() { return issuedByAccountId; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getConsumedAt() { return consumedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    public void consume(Instant now) {
        this.consumedAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
    }
}
