package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class ProfileBlockJpaId implements Serializable {

    private UUID blockerAccountId;
    private UUID blockedAccountId;

    public ProfileBlockJpaId() {
    }

    public ProfileBlockJpaId(UUID blockerAccountId, UUID blockedAccountId) {
        this.blockerAccountId = blockerAccountId;
        this.blockedAccountId = blockedAccountId;
    }

    public UUID getBlockerAccountId() {
        return blockerAccountId;
    }

    public UUID getBlockedAccountId() {
        return blockedAccountId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProfileBlockJpaId that)) {
            return false;
        }
        return Objects.equals(blockerAccountId, that.blockerAccountId)
                && Objects.equals(blockedAccountId, that.blockedAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockerAccountId, blockedAccountId);
    }
}
