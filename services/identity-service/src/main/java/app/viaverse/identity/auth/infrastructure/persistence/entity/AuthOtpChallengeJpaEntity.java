package app.viaverse.identity.auth.infrastructure.persistence.entity;

import app.viaverse.identity.auth.domain.enums.OtpChallengeStatus;
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
            int attempts,
            int maxAttempts,
            OtpChallengeStatus status,
            Instant expiresAt,
            Instant verifiedAt,
            Instant createdAt
    ) {
        this.id = id;
        this.flowId = flowId;
        this.otpHash = otpHash;
        this.attempts = attempts;
        this.maxAttempts = maxAttempts;
        this.status = status;
        this.expiresAt = expiresAt;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getFlowId() { return flowId; }
    public String getOtpHash() { return otpHash; }
    public int getAttempts() { return attempts; }
    public int getMaxAttempts() { return maxAttempts; }
    public OtpChallengeStatus getStatus() { return status; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getVerifiedAt() { return verifiedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
