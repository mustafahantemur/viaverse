package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_invitation")
public class AdminInvitationJpaEntity {
    @Id
    private UUID id;

    @Column(name = "token_hash", nullable = false, length = 128, unique = true)
    private String tokenHash;

    @Column(name = "issued_by_account_id", nullable = false)
    private UUID issuedByAccountId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AdminInvitationJpaEntity() {
    }

    public AdminInvitationJpaEntity(
            UUID id,
            String tokenHash,
            UUID issuedByAccountId,
            Instant expiresAt,
            Instant consumedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.issuedByAccountId = issuedByAccountId;
        this.expiresAt = expiresAt;
        this.consumedAt = consumedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public UUID getIssuedByAccountId() { return issuedByAccountId; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getConsumedAt() { return consumedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
