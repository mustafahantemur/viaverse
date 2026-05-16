package app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_login_flow")
public class AuthLoginFlowJpaEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_type", nullable = false, length = 16)
    private IdentifierTypeEnum identifierType;

    @Column(name = "normalized_identifier", nullable = false, length = 320)
    private String normalizedIdentifier;

    @Column(name = "account_id")
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private LoginFlowStatusEnum status;

    @Column(name = "registration_token_hash", length = 128)
    private String registrationTokenHash;

    @Column(name = "registration_expires_at")
    private Instant registrationExpiresAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthLoginFlowJpaEntity() {
    }

    public AuthLoginFlowJpaEntity(
            UUID id,
            IdentifierTypeEnum identifierType,
            String normalizedIdentifier,
            UUID accountId,
            LoginFlowStatusEnum status,
            String registrationTokenHash,
            Instant registrationExpiresAt,
            Instant expiresAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.identifierType = identifierType;
        this.normalizedIdentifier = normalizedIdentifier;
        this.accountId = accountId;
        this.status = status;
        this.registrationTokenHash = registrationTokenHash;
        this.registrationExpiresAt = registrationExpiresAt;
        this.expiresAt = expiresAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public IdentifierTypeEnum getIdentifierType() { return identifierType; }
    public String getNormalizedIdentifier() { return normalizedIdentifier; }
    public UUID getAccountId() { return accountId; }
    public LoginFlowStatusEnum getStatus() { return status; }
    public String getRegistrationTokenHash() { return registrationTokenHash; }
    public Instant getRegistrationExpiresAt() { return registrationExpiresAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCompletedAt() { return completedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
