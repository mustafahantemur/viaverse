package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.SessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_session")
public class AuthSessionJpaEntity {
    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private SessionStatus status;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthSessionJpaEntity() {
    }

    public AuthSessionJpaEntity(UUID id, UUID accountId, Instant now, Instant expiresAt, String userAgent) {
        this.id = id;
        this.accountId = accountId;
        this.status = SessionStatus.ACTIVE;
        this.issuedAt = now;
        this.expiresAt = expiresAt;
        this.lastSeenAt = now;
        this.userAgent = userAgent;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void touch(Instant now) {
        this.lastSeenAt = now;
        this.updatedAt = now;
    }

    public void revoke(Instant now) {
        this.status = SessionStatus.REVOKED;
        this.revokedAt = now;
        this.updatedAt = now;
    }

    public void expire(Instant now) {
        this.status = SessionStatus.EXPIRED;
        this.updatedAt = now;
    }
}
