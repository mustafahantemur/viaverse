package app.viaverse.identity.auth.infrastructure.persistence.entity;

import app.viaverse.identity.auth.domain.enums.IdentifierType;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
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
    private IdentifierType identifierType;

    @Column(name = "normalized_identifier", nullable = false, length = 320)
    private String normalizedIdentifier;

    @Column(name = "account_id")
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private LoginFlowStatus status;

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
            IdentifierType identifierType,
            String normalizedIdentifier,
            UUID accountId,
            LoginFlowStatus status,
            Instant expiresAt,
            Instant now
    ) {
        this.id = id;
        this.identifierType = identifierType;
        this.normalizedIdentifier = normalizedIdentifier;
        this.accountId = accountId;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public String getNormalizedIdentifier() {
        return normalizedIdentifier;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public LoginFlowStatus getStatus() {
        return status;
    }

    public String getRegistrationTokenHash() {
        return registrationTokenHash;
    }

    public Instant getRegistrationExpiresAt() {
        return registrationExpiresAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void markOtpVerified(Instant now) {
        this.status = LoginFlowStatus.OTP_VERIFIED;
        this.updatedAt = now;
    }

    public void requireRegistration(String registrationTokenHash, Instant registrationExpiresAt, Instant now) {
        this.status = LoginFlowStatus.REGISTRATION_REQUIRED;
        this.registrationTokenHash = registrationTokenHash;
        this.registrationExpiresAt = registrationExpiresAt;
        this.updatedAt = now;
    }

    public void complete(UUID accountId, Instant now) {
        this.status = LoginFlowStatus.COMPLETED;
        this.accountId = accountId;
        this.completedAt = now;
        this.updatedAt = now;
    }

    public void fail(LoginFlowStatus status, Instant now) {
        this.status = status;
        this.updatedAt = now;
    }
}
