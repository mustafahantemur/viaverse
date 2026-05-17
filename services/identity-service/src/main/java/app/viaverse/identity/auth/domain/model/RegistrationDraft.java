package app.viaverse.identity.auth.domain.model;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Server-side staging record for a multi-step registration. Lives in
 * Valkey (never on disk) with a short TTL — gone if the user abandons.
 *
 * <p>Holds everything the form collected, plus per-identifier verification
 * state. {@code passwordHash} is already encoded by the encoder before the
 * draft is created so plaintext never sits in the cache.
 *
 * <p>The two {@code flowId} fields point at {@link AuthLoginFlow} rows that
 * own the actual OTP state (rate-limit buckets, attempts, expiry). The
 * draft just remembers which flow proves which identifier.
 */
public final class RegistrationDraft {

    private final UUID id;
    private final String email;
    private final String phone;
    private final String displayName;
    private final String firstName;
    private final String lastName;
    private final String passwordHash;
    private final List<ConsentTypeEnum> acceptedRequiredConsents;
    private final boolean marketingConsentAccepted;
    private final UUID emailFlowId;
    private final Instant createdAt;

    private Instant emailVerifiedAt;
    private UUID phoneFlowId;
    private Instant phoneVerifiedAt;

    public RegistrationDraft(
            UUID id,
            String email,
            String phone,
            String displayName,
            String firstName,
            String lastName,
            String passwordHash,
            List<ConsentTypeEnum> acceptedRequiredConsents,
            boolean marketingConsentAccepted,
            UUID emailFlowId,
            Instant emailVerifiedAt,
            UUID phoneFlowId,
            Instant phoneVerifiedAt,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = Objects.requireNonNull(email, "email");
        this.phone = phone;
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.acceptedRequiredConsents = List.copyOf(
                Objects.requireNonNull(acceptedRequiredConsents, "acceptedRequiredConsents"));
        this.marketingConsentAccepted = marketingConsentAccepted;
        this.emailFlowId = Objects.requireNonNull(emailFlowId, "emailFlowId");
        this.emailVerifiedAt = emailVerifiedAt;
        this.phoneFlowId = phoneFlowId;
        this.phoneVerifiedAt = phoneVerifiedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static RegistrationDraft draft(
            UUID id,
            String email,
            String phone,
            String displayName,
            String firstName,
            String lastName,
            String passwordHash,
            List<ConsentTypeEnum> acceptedRequiredConsents,
            boolean marketingConsentAccepted,
            UUID emailFlowId,
            Instant createdAt
    ) {
        return new RegistrationDraft(
                id, email, phone, displayName, firstName, lastName, passwordHash,
                acceptedRequiredConsents, marketingConsentAccepted,
                emailFlowId, null, null, null, createdAt
        );
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDisplayName() { return displayName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPasswordHash() { return passwordHash; }
    public List<ConsentTypeEnum> getAcceptedRequiredConsents() { return acceptedRequiredConsents; }
    public boolean isMarketingConsentAccepted() { return marketingConsentAccepted; }
    public UUID getEmailFlowId() { return emailFlowId; }
    public Instant getEmailVerifiedAt() { return emailVerifiedAt; }
    public UUID getPhoneFlowId() { return phoneFlowId; }
    public Instant getPhoneVerifiedAt() { return phoneVerifiedAt; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean hasPhone() {
        return phone != null && !phone.isBlank();
    }

    public boolean isEmailVerified() {
        return emailVerifiedAt != null;
    }

    public boolean isPhoneVerified() {
        return phoneVerifiedAt != null;
    }

    public void markEmailVerified(Instant now) {
        if (emailVerifiedAt != null) {
            throw new IllegalStateException("Email already verified");
        }
        this.emailVerifiedAt = Objects.requireNonNull(now, "now");
    }

    public void attachPhoneFlow(UUID phoneFlowId) {
        this.phoneFlowId = Objects.requireNonNull(phoneFlowId, "phoneFlowId");
    }

    public void markPhoneVerified(Instant now) {
        if (phoneVerifiedAt != null) {
            throw new IllegalStateException("Phone already verified");
        }
        this.phoneVerifiedAt = Objects.requireNonNull(now, "now");
    }
}
