package app.viaverse.identity.account.domain.model;

import app.viaverse.identity.account.domain.AccountStatusEnum;
import app.viaverse.identity.account.domain.AccountRoleEnum;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Domain model representing a user account in the identity context.
 * <p>
 * Pure Java class — no JPA / Spring annotations. Mostly a data carrier, but encapsulates
 * profile-completion state, password lifecycle, 2FA state and lifecycle transitions
 * (suspend / reactivate) so that persistence adapters and use cases never mutate fields
 * directly.
 *
 * <p>{@code passwordHash} may be {@code null} for social-only accounts (registered via
 * Google / Apple). Such accounts can later add a password via the {@code /me/password}
 * endpoint. {@code twoFactorSecret} is the AES-GCM-encrypted TOTP shared secret; only
 * the identity-service holds the decryption key.
 */
public final class Account {

    private final UUID id;
    private final Instant createdAt;

    private AccountStatusEnum status;
    private Set<AccountRoleEnum> roles;
    private String displayName;
    private String firstName;
    private String lastName;
    private boolean profileCompleted;
    private String passwordHash;
    private Instant passwordUpdatedAt;
    private boolean twoFactorEnabled;
    private byte[] twoFactorSecret;
    private Instant twoFactorEnrolledAt;
    private Instant updatedAt;

    public Account(
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
        this.id = Objects.requireNonNull(id, "id");
        this.status = Objects.requireNonNull(status, "status");
        this.roles = EnumSet.copyOf(Objects.requireNonNull(roles, "roles"));
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileCompleted = profileCompleted;
        this.passwordHash = passwordHash;
        this.passwordUpdatedAt = passwordUpdatedAt;
        this.twoFactorEnabled = twoFactorEnabled;
        this.twoFactorSecret = twoFactorSecret == null ? null : twoFactorSecret.clone();
        this.twoFactorEnrolledAt = twoFactorEnrolledAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    /**
     * Factory for newly-registered accounts. Starts in {@link AccountStatusEnum#ACTIVE}
     * with {@code profileCompleted=false}, USER role, no password, no 2FA. Use
     * {@link #setPassword(String, Instant)} immediately after if the registration flow
     * required one.
     */
    public static Account register(UUID id, String displayName, Instant now) {
        return new Account(
                id,
                AccountStatusEnum.ACTIVE,
                Set.of(AccountRoleEnum.USER),
                displayName,
                null,
                null,
                false,
                null,
                null,
                false,
                null,
                null,
                now,
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public AccountStatusEnum getStatus() {
        return status;
    }

    public Set<AccountRoleEnum> getRoles() {
        return Set.copyOf(roles);
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public byte[] getTwoFactorSecret() {
        return twoFactorSecret == null ? null : twoFactorSecret.clone();
    }

    public Instant getTwoFactorEnrolledAt() {
        return twoFactorEnrolledAt;
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
     * Mirrors display fields owned by profile-service so legacy identity reads stay
     * compatible while richer user-facing reads move to profile-service.
     */
    public void mirrorDisplayFields(String displayName, String firstName, String lastName, Instant now) {
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.firstName = firstName;
        this.lastName = lastName;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Transition the account into {@link AccountStatusEnum#SUSPENDED}.
     */
    public void suspend(Instant now) {
        this.status = AccountStatusEnum.SUSPENDED;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Transition the account back into {@link AccountStatusEnum#ACTIVE}.
     */
    public void reactivate(Instant now) {
        this.status = AccountStatusEnum.ACTIVE;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    public void grantRole(AccountRoleEnum role, Instant now) {
        this.roles.add(Objects.requireNonNull(role, "role"));
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Set or rotate the password hash. The hash format (Argon2id) is encoded in the
     * string itself so we can rotate algorithm parameters without a schema change.
     */
    public void setPassword(String passwordHash, Instant now) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash must not be blank");
        }
        this.passwordHash = passwordHash;
        this.passwordUpdatedAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
    }

    /**
     * Enable TOTP-based 2FA with the supplied encrypted shared secret. The caller
     * is responsible for encrypting the secret with the identity-service key.
     */
    public void enableTwoFactor(byte[] encryptedSecret, Instant now) {
        if (encryptedSecret == null || encryptedSecret.length == 0) {
            throw new IllegalArgumentException("encryptedSecret must not be empty");
        }
        this.twoFactorSecret = encryptedSecret.clone();
        this.twoFactorEnabled = true;
        this.twoFactorEnrolledAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
    }

    /**
     * Disable 2FA and clear the stored secret. Called by {@code DELETE /me/2fa} after
     * proof-of-ownership checks succeed.
     */
    public void disableTwoFactor(Instant now) {
        this.twoFactorEnabled = false;
        this.twoFactorSecret = null;
        this.twoFactorEnrolledAt = null;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }
}
