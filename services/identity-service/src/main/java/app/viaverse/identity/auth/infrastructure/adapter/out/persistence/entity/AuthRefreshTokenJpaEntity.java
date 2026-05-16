package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity;

import app.viaverse.identity.auth.domain.enums.RefreshTokenStatusEnum;
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
    private RefreshTokenStatusEnum status;

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

    public AuthRefreshTokenJpaEntity(
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
        this.id = id;
        this.sessionId = sessionId;
        this.tokenHash = tokenHash;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.replacedByTokenId = replacedByTokenId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public String getTokenHash() { return tokenHash; }
    public RefreshTokenStatusEnum getStatus() { return status; }
    public Instant getIssuedAt() { return issuedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public UUID getReplacedByTokenId() { return replacedByTokenId; }
    public Instant getCreatedAt() { return createdAt; }
}
