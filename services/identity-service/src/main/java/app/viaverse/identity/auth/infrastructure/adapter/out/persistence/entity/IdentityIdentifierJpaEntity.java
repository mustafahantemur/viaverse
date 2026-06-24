package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "identity_identifier")
public class IdentityIdentifierJpaEntity {
    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_type", nullable = false, length = 16)
    private IdentifierTypeEnum identifierType;

    @Column(name = "normalized_identifier", nullable = false, length = 320)
    private String normalizedIdentifier;

    @Column(name = "verified_at", nullable = false)
    private Instant verifiedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected IdentityIdentifierJpaEntity() {
    }

    public IdentityIdentifierJpaEntity(
            UUID id,
            UUID accountId,
            IdentifierTypeEnum identifierType,
            String normalizedIdentifier,
            Instant verifiedAt,
            Instant createdAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.identifierType = identifierType;
        this.normalizedIdentifier = normalizedIdentifier;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getId() { return id; }
    public IdentifierTypeEnum getIdentifierType() { return identifierType; }
    public String getNormalizedIdentifier() { return normalizedIdentifier; }
    public Instant getVerifiedAt() { return verifiedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
