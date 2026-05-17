package app.viaverse.identity.auth.domain.model;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing an authentication login flow.
 * <p>
 * Pure Java class — no JPA / Spring annotations. Encapsulates state transitions
 * (OTP verification, registration handoff, completion, failure) so that persistence
 * adapters and use cases never mutate fields directly.
 *
 * <p>Each flow carries a {@link LoginFlowPurposeEnum} so a registration OTP
 * cannot accidentally satisfy a 2FA-setup challenge or vice versa, even if a
 * client replays the {@code flowId}.
 */
public final class AuthLoginFlow {

    private final UUID id;
    private final LoginFlowPurposeEnum purpose;
    private final IdentifierTypeEnum identifierType;
    private final String normalizedIdentifier;
    private final boolean externalVerified;
    private final Instant expiresAt;
    private final Instant createdAt;

    private UUID accountId;
    private LoginFlowStatusEnum status;
    private String registrationTokenHash;
    private Instant registrationExpiresAt;
    private Instant completedAt;
    private Instant updatedAt;

    public AuthLoginFlow(
            UUID id,
            LoginFlowPurposeEnum purpose,
            IdentifierTypeEnum identifierType,
            String normalizedIdentifier,
            UUID accountId,
            LoginFlowStatusEnum status,
            boolean externalVerified,
            String registrationTokenHash,
            Instant registrationExpiresAt,
            Instant expiresAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.purpose = Objects.requireNonNull(purpose, "purpose");
        this.identifierType = Objects.requireNonNull(identifierType, "identifierType");
        this.normalizedIdentifier = Objects.requireNonNull(normalizedIdentifier, "normalizedIdentifier");
        this.accountId = accountId;
        this.status = Objects.requireNonNull(status, "status");
        this.externalVerified = externalVerified;
        this.registrationTokenHash = registrationTokenHash;
        this.registrationExpiresAt = registrationExpiresAt;
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.completedAt = completedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    /**
     * Factory for OTP-driven flows (registration, identifier-verify, 2FA setup,
     * password reset). Lands in {@link LoginFlowStatusEnum#OTP_REQUIRED}.
     */
    public static AuthLoginFlow issue(
            UUID id,
            LoginFlowPurposeEnum purpose,
            IdentifierTypeEnum identifierType,
            String normalizedIdentifier,
            UUID accountId,
            Instant expiresAt,
            Instant now
    ) {
        return new AuthLoginFlow(
                id,
                purpose,
                identifierType,
                normalizedIdentifier,
                accountId,
                LoginFlowStatusEnum.OTP_REQUIRED,
                false,
                null,
                null,
                expiresAt,
                null,
                now,
                now
        );
    }

    /**
     * Factory for identities already verified by an external IdP (e.g. Google, Apple).
     * Lands in {@link LoginFlowStatusEnum#EXTERNAL_VERIFIED} so {@code OTP_VERIFIED}
     * keeps its invariant of "an OTP was actually consumed". Always REGISTRATION purpose.
     */
    public static AuthLoginFlow issueExternallyVerified(
            UUID id,
            IdentifierTypeEnum identifierType,
            String normalizedIdentifier,
            UUID accountId,
            Instant expiresAt,
            Instant now
    ) {
        return new AuthLoginFlow(
                id,
                LoginFlowPurposeEnum.REGISTRATION,
                identifierType,
                normalizedIdentifier,
                accountId,
                LoginFlowStatusEnum.EXTERNAL_VERIFIED,
                true,
                null,
                null,
                expiresAt,
                null,
                now,
                now
        );
    }

    public boolean isExternalVerified() {
        return externalVerified;
    }

    public UUID getId() {
        return id;
    }

    public LoginFlowPurposeEnum getPurpose() {
        return purpose;
    }

    public IdentifierTypeEnum getIdentifierType() {
        return identifierType;
    }

    public String getNormalizedIdentifier() {
        return normalizedIdentifier;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public LoginFlowStatusEnum getStatus() {
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

    public void markOtpVerified(Instant now) {
        this.status = LoginFlowStatusEnum.OTP_VERIFIED;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    public void requireRegistration(String registrationTokenHash, Instant registrationExpiresAt, Instant now) {
        this.status = LoginFlowStatusEnum.REGISTRATION_REQUIRED;
        this.registrationTokenHash = Objects.requireNonNull(registrationTokenHash, "registrationTokenHash");
        this.registrationExpiresAt = Objects.requireNonNull(registrationExpiresAt, "registrationExpiresAt");
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    public void complete(UUID accountId, Instant now) {
        this.status = LoginFlowStatusEnum.COMPLETED;
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.completedAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
    }

    public void fail(LoginFlowStatusEnum failureStatus, Instant now) {
        this.status = Objects.requireNonNull(failureStatus, "failureStatus");
        this.updatedAt = Objects.requireNonNull(now, "now");
    }
}
