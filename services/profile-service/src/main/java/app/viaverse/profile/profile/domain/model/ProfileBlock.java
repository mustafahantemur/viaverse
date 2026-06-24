package app.viaverse.profile.profile.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Directed block relationship owned by the blocking account.
 */
public final class ProfileBlock {

    private final UUID blockerAccountId;
    private final UUID blockedAccountId;
    private final String reason;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public ProfileBlock(
            UUID blockerAccountId,
            UUID blockedAccountId,
            String reason,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.blockerAccountId = Objects.requireNonNull(blockerAccountId, "blockerAccountId");
        this.blockedAccountId = Objects.requireNonNull(blockedAccountId, "blockedAccountId");
        if (blockerAccountId.equals(blockedAccountId)) {
            throw new IllegalArgumentException("an account cannot block itself");
        }
        if (reason != null && reason.length() > 200) {
            throw new IllegalArgumentException("reason must not exceed 200 characters");
        }
        this.reason = reason;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public UUID getBlockerAccountId() {
        return blockerAccountId;
    }

    public UUID getBlockedAccountId() {
        return blockedAccountId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
