package app.viaverse.identity.auth.domain.model;

import app.viaverse.identity.auth.domain.enums.OtpChallengeStatusEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model representing a one-time-password challenge bound to an {@link AuthLoginFlow}.
 * <p>
 * Pure Java — no JPA / Spring annotations. Tracks failed attempts and transitions to terminal
 * states (LOCKED, EXPIRED, VERIFIED).
 */
public final class OtpChallenge {

    private final UUID id;
    private final UUID flowId;
    private final String otpHash;
    private final int maxAttempts;
    private final Instant expiresAt;
    private final Instant createdAt;

    private int attempts;
    private OtpChallengeStatusEnum status;
    private Instant verifiedAt;

    public OtpChallenge(
            UUID id,
            UUID flowId,
            String otpHash,
            int attempts,
            int maxAttempts,
            OtpChallengeStatusEnum status,
            Instant expiresAt,
            Instant verifiedAt,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.flowId = Objects.requireNonNull(flowId, "flowId");
        this.otpHash = Objects.requireNonNull(otpHash, "otpHash");
        this.attempts = attempts;
        this.maxAttempts = maxAttempts;
        this.status = Objects.requireNonNull(status, "status");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.verifiedAt = verifiedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    /**
     * Factory for a freshly-issued challenge in the {@code ACTIVE} state.
     */
    public static OtpChallenge issue(
            UUID id,
            UUID flowId,
            String otpHash,
            int maxAttempts,
            Instant expiresAt,
            Instant now
    ) {
        return new OtpChallenge(
                id,
                flowId,
                otpHash,
                0,
                maxAttempts,
                OtpChallengeStatusEnum.ACTIVE,
                expiresAt,
                null,
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getFlowId() {
        return flowId;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public OtpChallengeStatusEnum getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Whether the challenge can still accept a verification attempt.
     */
    public boolean isActive() {
        return status == OtpChallengeStatusEnum.ACTIVE;
    }

    /**
     * Record a failed verification attempt. Auto-transitions to {@code LOCKED}
     * once the max-attempts threshold is reached.
     */
    public void recordFailure() {
        this.attempts++;
        if (attempts >= maxAttempts) {
            this.status = OtpChallengeStatusEnum.LOCKED;
        }
    }

    /**
     * Transition into the terminal EXPIRED state.
     */
    public void expire() {
        this.status = OtpChallengeStatusEnum.EXPIRED;
    }

    /**
     * Transition into the terminal VERIFIED state and stamp verification time.
     */
    public void verify(Instant now) {
        this.status = OtpChallengeStatusEnum.VERIFIED;
        this.verifiedAt = Objects.requireNonNull(now, "now");
    }
}
