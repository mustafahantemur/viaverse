package app.viaverse.profile.profile.domain.model;

import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import app.viaverse.profile.profile.domain.enums.ProfileCapabilityStatusEnum;
import app.viaverse.profile.profile.domain.enums.ProviderVerificationLevelEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ProfileCapability {

    private final UUID accountId;
    private final ProfileCapabilityEnum capability;
    private final ProfileCapabilityStatusEnum status;
    private final Instant enabledAt;
    private final Instant disabledAt;
    private final ProviderVerificationLevelEnum verificationLevel;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public ProfileCapability(
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
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.capability = Objects.requireNonNull(capability, "capability");
        this.status = Objects.requireNonNull(status, "status");
        this.enabledAt = enabledAt;
        this.disabledAt = disabledAt;
        this.verificationLevel = Objects.requireNonNull(verificationLevel, "verificationLevel");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static ProfileCapability customerEnabled(UUID accountId, Instant now) {
        return new ProfileCapability(
                accountId,
                ProfileCapabilityEnum.CUSTOMER,
                ProfileCapabilityStatusEnum.ENABLED,
                now,
                null,
                ProviderVerificationLevelEnum.NONE,
                now,
                now,
                0
        );
    }

    public static ProfileCapability individualProviderEnabled(UUID accountId, Instant now) {
        return new ProfileCapability(
                accountId,
                ProfileCapabilityEnum.INDIVIDUAL_PROVIDER,
                ProfileCapabilityStatusEnum.ENABLED,
                now,
                null,
                ProviderVerificationLevelEnum.NONE,
                now,
                now,
                0
        );
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public boolean isEnabled() {
        return status == ProfileCapabilityStatusEnum.ENABLED;
    }

    public ProfileCapability enable(Instant now) {
        return new ProfileCapability(
                accountId,
                capability,
                ProfileCapabilityStatusEnum.ENABLED,
                enabledAt == null ? now : enabledAt,
                null,
                verificationLevel,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    public ProfileCapability disable(Instant now) {
        return new ProfileCapability(
                accountId,
                capability,
                ProfileCapabilityStatusEnum.DISABLED,
                enabledAt,
                Objects.requireNonNull(now, "now"),
                verificationLevel,
                createdAt,
                now,
                version
        );
    }
}
