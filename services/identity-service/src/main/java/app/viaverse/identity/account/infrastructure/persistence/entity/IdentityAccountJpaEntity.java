package app.viaverse.identity.account.infrastructure.persistence.entity;

import app.viaverse.identity.account.domain.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "identity_account")
public class IdentityAccountJpaEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AccountStatus status;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected IdentityAccountJpaEntity() {
    }

    public IdentityAccountJpaEntity(
            UUID id,
            AccountStatus status,
            String displayName,
            String firstName,
            String lastName,
            boolean profileCompleted,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.status = status;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileCompleted = profileCompleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
