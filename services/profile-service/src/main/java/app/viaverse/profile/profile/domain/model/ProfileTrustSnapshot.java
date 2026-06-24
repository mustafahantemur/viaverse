package app.viaverse.profile.profile.domain.model;

import app.viaverse.profile.profile.domain.enums.TrustBadgeEnum;
import app.viaverse.profile.profile.domain.enums.TrustLevelEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ProfileTrustSnapshot {

    private final UUID accountId;
    private final int score;
    private final TrustLevelEnum level;
    private final TrustBadgeEnum badge;
    private final Instant sourceOccurredAt;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public ProfileTrustSnapshot(
            UUID accountId,
            int score,
            TrustLevelEnum level,
            TrustBadgeEnum badge,
            Instant sourceOccurredAt,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        if (score < 0 || score > 1000) {
            throw new IllegalArgumentException("score must be between 0 and 1000");
        }
        this.score = score;
        this.level = Objects.requireNonNull(level, "level");
        this.badge = Objects.requireNonNull(badge, "badge");
        this.sourceOccurredAt = Objects.requireNonNull(sourceOccurredAt, "sourceOccurredAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static ProfileTrustSnapshot fromTrustScore(
            UUID accountId,
            int score,
            TrustLevelEnum level,
            TrustBadgeEnum badge,
            Instant occurredAt
    ) {
        return new ProfileTrustSnapshot(accountId, score, level, badge, occurredAt, occurredAt, occurredAt, 0);
    }

    public ProfileTrustSnapshot updateFromTrustScore(
            int nextScore,
            TrustLevelEnum nextLevel,
            TrustBadgeEnum nextBadge,
            Instant occurredAt
    ) {
        return new ProfileTrustSnapshot(
                accountId,
                nextScore,
                nextLevel,
                nextBadge,
                occurredAt,
                createdAt,
                occurredAt,
                version
        );
    }

    public UUID getAccountId() {
        return accountId;
    }

    public int getScore() {
        return score;
    }

    public TrustLevelEnum getLevel() {
        return level;
    }

    public TrustBadgeEnum getBadge() {
        return badge;
    }

    public Instant getSourceOccurredAt() {
        return sourceOccurredAt;
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
