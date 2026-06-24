package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.profile.profile.domain.enums.ProfileCapabilityEnum;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class ProfileCapabilityJpaId implements Serializable {

    private UUID accountId;
    private ProfileCapabilityEnum capability;

    public ProfileCapabilityJpaId() {
    }

    public ProfileCapabilityJpaId(UUID accountId, ProfileCapabilityEnum capability) {
        this.accountId = accountId;
        this.capability = capability;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public ProfileCapabilityEnum getCapability() {
        return capability;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProfileCapabilityJpaId that)) {
            return false;
        }
        return Objects.equals(accountId, that.accountId) && capability == that.capability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, capability);
    }
}
