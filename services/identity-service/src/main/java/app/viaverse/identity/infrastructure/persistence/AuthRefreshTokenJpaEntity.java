package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.RefreshTokenStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_refresh_token")
public class AuthRefreshTokenJpaEntity {
    @Id
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "token_hash", nullable = false, length = 128)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RefreshTokenStatus status;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by_token_id")
    private UUID replacedByTokenId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuthRefreshTokenJpaEntity() {
    }

    public AuthRefreshTokenJpaEntity(UUID id, UUID sessionId, String tokenHash, Instant now, Instant expiresAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.tokenHash = tokenHash;
        this.status = RefreshTokenStatus.ACTIVE;
        this.issuedAt = now;
        this.expiresAt = expiresAt;
        this.createdAt = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public RefreshTokenStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void rotate(UUID replacementTokenId, Instant now) {
        this.status = RefreshTokenStatus.ROTATED;
        this.replacedByTokenId = replacementTokenId;
        this.revokedAt = now;
    }

    public void revoke(Instant now) {
        this.status = RefreshTokenStatus.REVOKED;
        this.revokedAt = now;
    }

    public void expire(Instant now) {
        this.status = RefreshTokenStatus.EXPIRED;
        this.revokedAt = now;
    }
}
