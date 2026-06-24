package app.viaverse.identity.auth.domain.model;

import app.viaverse.identity.auth.domain.enums.SessionStatusEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class AuthSession {

    private final UUID id;
    private final UUID accountId;
    private final Instant issuedAt;
    private final Instant createdAt;

    private SessionStatusEnum status;
    private Instant expiresAt;
    private Instant lastSeenAt;
    private Instant revokedAt;
    private String userAgent;
    private String deviceId;
    private String deviceName;
    private String platform;
    private String lastIp;
    private Instant updatedAt;

    public AuthSession(
            UUID id,
            UUID accountId,
            SessionStatusEnum status,
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
        this.id = Objects.requireNonNull(id, "id");
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.status = Objects.requireNonNull(status, "status");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.lastSeenAt = lastSeenAt;
        this.revokedAt = revokedAt;
        this.userAgent = userAgent;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.platform = platform;
        this.lastIp = lastIp;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static AuthSession issue(
            UUID id,
            UUID accountId,
            Instant expiresAt,
            String userAgent,
            String deviceId,
            String deviceName,
            String platform,
            String lastIp,
            Instant now
    ) {
        return new AuthSession(
                id, accountId, SessionStatusEnum.ACTIVE,
                now, expiresAt, now, null,
                userAgent, deviceId, deviceName, platform, lastIp,
                now, now
        );
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public SessionStatusEnum getStatus() { return status; }
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

    public void touch(Instant now) {
        this.lastSeenAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
    }

    public void revoke(Instant now) {
        this.status = SessionStatusEnum.REVOKED;
        this.revokedAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
    }

    public void expire(Instant now) {
        this.status = SessionStatusEnum.EXPIRED;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }
}
