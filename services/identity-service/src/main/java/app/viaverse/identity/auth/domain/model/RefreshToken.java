package app.viaverse.identity.auth.domain.model;

import app.viaverse.identity.auth.domain.enums.RefreshTokenStatusEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing a refresh token issued for an active {@code AuthSession}.
 * <p>
 * Pure Java — no JPA / Spring annotations. Encapsulates the rotate / revoke / expire
 * state transitions that previously lived on the JPA entity.
 */
public final class RefreshToken {

    private final UUID id;
    private final UUID sessionId;
    private final String tokenHash;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final Instant createdAt;

    private RefreshTokenStatusEnum status;
    private Instant revokedAt;
    private UUID replacedByTokenId;

    public RefreshToken(
            UUID id,
            UUID sessionId,
            String tokenHash,
            RefreshTokenStatusEnum status,
            Instant issuedAt,
            Instant expiresAt,
            Instant revokedAt,
            UUID replacedByTokenId,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        this.status = Objects.requireNonNull(status, "status");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.revokedAt = revokedAt;
        this.replacedByTokenId = replacedByTokenId;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    /**
     * Factory for a freshly-issued token in the {@code ACTIVE} state.
     */
    public static RefreshToken issue(UUID id, UUID sessionId, String tokenHash, Instant now, Instant expiresAt) {
        return new RefreshToken(
                id,
                sessionId,
                tokenHash,
                RefreshTokenStatusEnum.ACTIVE,
                now,
                expiresAt,
                null,
                null,
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public RefreshTokenStatusEnum getStatus() {
        return status;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public UUID getReplacedByTokenId() {
        return replacedByTokenId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return status == RefreshTokenStatusEnum.ACTIVE;
    }

    /**
     * Rotate this token — caller has issued {@code replacementTokenId} as the successor.
     */
    public void rotate(UUID replacementTokenId, Instant now) {
        this.status = RefreshTokenStatusEnum.ROTATED;
        this.replacedByTokenId = Objects.requireNonNull(replacementTokenId, "replacementTokenId");
        this.revokedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Explicitly revoke this token (logout, session-revoke, security event).
     */
    public void revoke(Instant now) {
        this.status = RefreshTokenStatusEnum.REVOKED;
        this.revokedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Mark this token as expired due to TTL.
     */
    public void expire(Instant now) {
        this.status = RefreshTokenStatusEnum.EXPIRED;
        this.revokedAt = Objects.requireNonNull(now, "now");
    }
}
