package app.viaverse.identity.auth.domain.model;

import app.viaverse.identity.auth.domain.enums.IdentifierType;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing an authentication login flow.
 * <p>
 * Pure Java class — no JPA / Spring annotations. Encapsulates state transitions
 * (OTP verification, registration handoff, completion, failure) so that persistence
 * adapters and use cases never mutate fields directly.
 */
public final class AuthLoginFlow {

    private final UUID id;
    private final IdentifierType identifierType;
    private final String normalizedIdentifier;
    private final Instant expiresAt;
    private final Instant createdAt;

    private UUID accountId;
    private LoginFlowStatus status;
    private String registrationTokenHash;
    private Instant registrationExpiresAt;
    private Instant completedAt;
    private Instant updatedAt;

    public AuthLoginFlow(
            UUID id,
            IdentifierType identifierType,
            String normalizedIdentifier,
            UUID accountId,
            LoginFlowStatus status,
            String registrationTokenHash,
            Instant registrationExpiresAt,
            Instant expiresAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.identifierType = Objects.requireNonNull(identifierType, "identifierType");
        this.normalizedIdentifier = Objects.requireNonNull(normalizedIdentifier, "normalizedIdentifier");
        this.accountId = accountId;
        this.status = Objects.requireNonNull(status, "status");
        this.registrationTokenHash = registrationTokenHash;
        this.registrationExpiresAt = registrationExpiresAt;
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.completedAt = completedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    /**
     * Factory for newly-issued flows in the OTP_REQUIRED state.
     */
    public static AuthLoginFlow issue(
            UUID id,
            IdentifierType identifierType,
            String normalizedIdentifier,
            UUID accountId,
            Instant expiresAt,
            Instant now
    ) {
        return new AuthLoginFlow(
                id,
                identifierType,
                normalizedIdentifier,
                accountId,
                LoginFlowStatus.OTP_REQUIRED,
                null,
                null,
                expiresAt,
                null,
                now,
                now
        );
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

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Mark this flow as having a verified OTP. Caller is expected to subsequently
     * either {@link #complete} (existing account) or {@link #requireRegistration} (new account).
     */
    public void markOtpVerified(Instant now) {
        this.status = LoginFlowStatus.OTP_VERIFIED;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Hand off to the registration step — caller has already issued a registration token.
     */
    public void requireRegistration(String registrationTokenHash, Instant registrationExpiresAt, Instant now) {
        this.status = LoginFlowStatus.REGISTRATION_REQUIRED;
        this.registrationTokenHash = Objects.requireNonNull(registrationTokenHash, "registrationTokenHash");
        this.registrationExpiresAt = Objects.requireNonNull(registrationExpiresAt, "registrationExpiresAt");
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Complete the flow — bind the resolved account and stamp completion time.
     */
    public void complete(UUID accountId, Instant now) {
        this.status = LoginFlowStatus.COMPLETED;
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.completedAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
    }

    /**
     * Transition into a terminal failure state (e.g. {@code OTP_FAILED}, {@code EXPIRED}).
     */
    public void fail(LoginFlowStatus failureStatus, Instant now) {
        this.status = Objects.requireNonNull(failureStatus, "failureStatus");
        this.updatedAt = Objects.requireNonNull(now, "now");
    }
}
