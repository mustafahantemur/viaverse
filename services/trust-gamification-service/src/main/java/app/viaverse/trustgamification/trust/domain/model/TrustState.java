package app.viaverse.trustgamification.trust.domain.model;

import app.viaverse.trustgamification.trust.domain.enums.TrustBadgeEnum;
import app.viaverse.trustgamification.trust.domain.enums.TrustLevelEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class TrustState {

    private final UUID accountId;
    private final int score;
    private final TrustLevelEnum level;
    private final TrustBadgeEnum badge;
    private final String scoreVersion;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public TrustState(
            UUID accountId,
            int score,
            TrustLevelEnum level,
            TrustBadgeEnum badge,
            String scoreVersion,
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
        this.scoreVersion = Objects.requireNonNull(scoreVersion, "scoreVersion");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static TrustState baseline(UUID accountId, Instant now) {
        return new TrustState(
                accountId,
                100,
                TrustLevelEnum.BASIC,
                TrustBadgeEnum.BASIC,
                "v1",
                now,
                now,
                0
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

    public String getScoreVersion() {
        return scoreVersion;
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
