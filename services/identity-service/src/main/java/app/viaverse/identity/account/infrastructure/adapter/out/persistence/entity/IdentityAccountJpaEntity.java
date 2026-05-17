package app.viaverse.identity.account.infrastructure.adapter.out.persistence.entity;

import app.viaverse.identity.account.domain.AccountStatusEnum;
import app.viaverse.identity.account.domain.AccountRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "identity_account")
public class IdentityAccountJpaEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AccountStatusEnum status;

    @ElementCollection(targetClass = AccountRoleEnum.class)
    @CollectionTable(name = "identity_account_role", joinColumns = @JoinColumn(name = "account_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    private Set<AccountRoleEnum> roles;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "password_updated_at")
    private Instant passwordUpdatedAt;

    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled;

    @Column(name = "two_factor_secret")
    private byte[] twoFactorSecret;

    @Column(name = "two_factor_enrolled_at")
    private Instant twoFactorEnrolledAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected IdentityAccountJpaEntity() {
    }

    public IdentityAccountJpaEntity(
            UUID id,
            AccountStatusEnum status,
            Set<AccountRoleEnum> roles,
            String displayName,
            String firstName,
            String lastName,
            boolean profileCompleted,
            String passwordHash,
            Instant passwordUpdatedAt,
            boolean twoFactorEnabled,
            byte[] twoFactorSecret,
            Instant twoFactorEnrolledAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.status = status;
        this.roles = roles;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileCompleted = profileCompleted;
        this.passwordHash = passwordHash;
        this.passwordUpdatedAt = passwordUpdatedAt;
        this.twoFactorEnabled = twoFactorEnabled;
        this.twoFactorSecret = twoFactorSecret;
        this.twoFactorEnrolledAt = twoFactorEnrolledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public AccountStatusEnum getStatus() { return status; }
    public Set<AccountRoleEnum> getRoles() { return roles; }
    public String getDisplayName() { return displayName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public boolean isProfileCompleted() { return profileCompleted; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getPasswordUpdatedAt() { return passwordUpdatedAt; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public byte[] getTwoFactorSecret() { return twoFactorSecret; }
    public Instant getTwoFactorEnrolledAt() { return twoFactorEnrolledAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
