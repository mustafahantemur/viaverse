package app.viaverse.trustgamification.trust.infrastructure.adapter.out.persistence.entity;

import app.viaverse.trustgamification.trust.domain.enums.TrustBadgeEnum;
import app.viaverse.trustgamification.trust.domain.enums.TrustLevelEnum;
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
@Table(name = "trust_state")
public class TrustStateJpaEntity extends BaseJpaEntity {

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

    @Column(name = "score_version", nullable = false, length = 64)
    private String scoreVersion;

    protected TrustStateJpaEntity() {
    }

    public TrustStateJpaEntity(
            UUID accountId,
            int score,
            TrustLevelEnum level,
            TrustBadgeEnum badge,
            String scoreVersion,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.accountId = accountId;
        this.score = score;
        this.level = level;
        this.badge = badge;
        this.scoreVersion = scoreVersion;
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
}
