package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.profile.profile.domain.enums.TrustBadgeEnum;
import app.viaverse.profile.profile.domain.enums.TrustLevelEnum;
import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profile_trust_snapshot")
public class ProfileTrustSnapshotJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "score", nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(name = "trust_level", nullable = false, length = 32)
    private TrustLevelEnum level;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge", nullable = false, length = 32)
    private TrustBadgeEnum badge;

    @Column(name = "source_occurred_at", nullable = false)
    private Instant sourceOccurredAt;

    protected ProfileTrustSnapshotJpaEntity() {
    }

    public ProfileTrustSnapshotJpaEntity(
            UUID accountId,
            int score,
            TrustLevelEnum level,
            TrustBadgeEnum badge,
            Instant sourceOccurredAt,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.accountId = accountId;
        this.score = score;
        this.level = level;
        this.badge = badge;
        this.sourceOccurredAt = sourceOccurredAt;
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
}
