package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.OtpChallengeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_otp_challenge")
public class AuthOtpChallengeJpaEntity {
    @Id
    private UUID id;

    @Column(name = "flow_id", nullable = false)
    private UUID flowId;

    @Column(name = "otp_hash", nullable = false, length = 128)
    private String otpHash;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OtpChallengeStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuthOtpChallengeJpaEntity() {
    }

    public AuthOtpChallengeJpaEntity(
            UUID id,
            UUID flowId,
            String otpHash,
            int maxAttempts,
            Instant expiresAt,
            Instant now
    ) {
        this.id = id;
        this.flowId = flowId;
        this.otpHash = otpHash;
        this.attempts = 0;
        this.maxAttempts = maxAttempts;
        this.status = OtpChallengeStatus.ACTIVE;
        this.expiresAt = expiresAt;
        this.createdAt = now;
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

    public OtpChallengeStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void recordFailure() {
        this.attempts++;
        if (attempts >= maxAttempts) {
            this.status = OtpChallengeStatus.LOCKED;
        }
    }

    public void expire() {
        this.status = OtpChallengeStatus.EXPIRED;
    }

    public void verify(Instant now) {
        this.status = OtpChallengeStatus.VERIFIED;
        this.verifiedAt = now;
    }
}
