package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@IdClass(ProfileBlockJpaId.class)
@Table(name = "profile_block")
public class ProfileBlockJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "blocker_account_id", nullable = false)
    private UUID blockerAccountId;

    @Id
    @Column(name = "blocked_account_id", nullable = false)
    private UUID blockedAccountId;

    @Column(name = "reason", length = 200)
    private String reason;

    protected ProfileBlockJpaEntity() {
    }

    public ProfileBlockJpaEntity(
            UUID blockerAccountId,
            UUID blockedAccountId,
            String reason,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.blockerAccountId = blockerAccountId;
        this.blockedAccountId = blockedAccountId;
        this.reason = reason;
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
}
