package app.viaverse.identity.account.domain.model;

import app.viaverse.identity.account.domain.AccountStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing a user account in the identity context.
 * <p>
 * Pure Java class — no JPA / Spring annotations. Mostly a data carrier, but encapsulates
 * profile-completion state and lifecycle transitions (suspend / reactivate) so that
 * persistence adapters and use cases never mutate fields directly.
 */
public final class Account {

    private final UUID id;
    private final Instant createdAt;

    private AccountStatus status;
    private String displayName;
    private String firstName;
    private String lastName;
    private boolean profileCompleted;
    private Instant updatedAt;

    public Account(
            UUID id,
            AccountStatus status,
            String displayName,
            String firstName,
            String lastName,
            boolean profileCompleted,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.status = Objects.requireNonNull(status, "status");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileCompleted = profileCompleted;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    /**
     * Factory for newly-registered accounts. Starts in {@link AccountStatus#ACTIVE}
     * with {@code profileCompleted=false} and no first/last name set.
     */
    public static Account register(UUID id, String displayName, Instant now) {
        return new Account(
                id,
                AccountStatus.ACTIVE,
                displayName,
                null,
                null,
                false,
                now,
                now
        );
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Fill in the user's profile details and mark profile completion. Updates the
     * display name as well, since profile completion is the canonical moment the
     * user finalises how they want to be addressed.
     */
    public void completeProfile(String firstName, String lastName, String displayName, Instant now) {
        this.firstName = Objects.requireNonNull(firstName, "firstName");
        this.lastName = Objects.requireNonNull(lastName, "lastName");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.profileCompleted = true;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Rename the account's display name without touching profile-completion state.
     */
    public void rename(String displayName, Instant now) {
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Transition the account into {@link AccountStatus#SUSPENDED}.
     */
    public void suspend(Instant now) {
        this.status = AccountStatus.SUSPENDED;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Transition the account back into {@link AccountStatus#ACTIVE}.
     */
    public void reactivate(Instant now) {
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }
}
