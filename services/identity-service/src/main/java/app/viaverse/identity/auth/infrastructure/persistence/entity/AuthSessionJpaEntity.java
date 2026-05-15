package app.viaverse.identity.auth.infrastructure.persistence.entity;

import app.viaverse.identity.auth.domain.enums.SessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_session")
public class AuthSessionJpaEntity {
    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private SessionStatus status;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "platform", length = 20)
    private String platform;

    @Column(name = "last_ip", length = 45)
    private String lastIp;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthSessionJpaEntity() {
    }

    public AuthSessionJpaEntity(
            UUID id,
            UUID accountId,
            SessionStatus status,
            Instant issuedAt,
            Instant expiresAt,
            Instant lastSeenAt,
            Instant revokedAt,
            String userAgent,
            String deviceId,
            String deviceName,
            String platform,
            String lastIp,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.lastSeenAt = lastSeenAt;
        this.revokedAt = revokedAt;
        this.userAgent = userAgent;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.platform = platform;
        this.lastIp = lastIp;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public SessionStatus getStatus() { return status; }
    public Instant getIssuedAt() { return issuedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getLastSeenAt() { return lastSeenAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public String getUserAgent() { return userAgent; }
    public String getDeviceId() { return deviceId; }
    public String getDeviceName() { return deviceName; }
    public String getPlatform() { return platform; }
    public String getLastIp() { return lastIp; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
