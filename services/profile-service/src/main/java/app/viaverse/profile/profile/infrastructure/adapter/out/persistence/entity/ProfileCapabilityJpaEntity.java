package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityStatusEnum;
import app.viaverse.profile.profile.domain.enums.ProviderVerificationLevelEnum;
import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@IdClass(ProfileCapabilityJpaId.class)
@Table(name = "profile_capability")
public class ProfileCapabilityJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "capability", nullable = false, length = 32)
    private ProfileCapabilityEnum capability;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ProfileCapabilityStatusEnum status;

    @Column(name = "enabled_at")
    private Instant enabledAt;

    @Column(name = "disabled_at")
    private Instant disabledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_level", nullable = false, length = 32)
    private ProviderVerificationLevelEnum verificationLevel;

    protected ProfileCapabilityJpaEntity() {
    }

    public ProfileCapabilityJpaEntity(
            UUID accountId,
            ProfileCapabilityEnum capability,
            ProfileCapabilityStatusEnum status,
            Instant enabledAt,
            Instant disabledAt,
            ProviderVerificationLevelEnum verificationLevel,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.accountId = accountId;
        this.capability = capability;
        this.status = status;
        this.enabledAt = enabledAt;
        this.disabledAt = disabledAt;
        this.verificationLevel = verificationLevel;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public ProfileCapabilityEnum getCapability() {
        return capability;
    }

    public ProfileCapabilityStatusEnum getStatus() {
        return status;
    }

    public Instant getEnabledAt() {
        return enabledAt;
    }

    public Instant getDisabledAt() {
        return disabledAt;
    }

    public ProviderVerificationLevelEnum getVerificationLevel() {
        return verificationLevel;
    }
}
