package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class ProfilePreferenceJpaId implements Serializable {

    private UUID accountId;
    private String key;

    public ProfilePreferenceJpaId() {
    }

    public ProfilePreferenceJpaId(UUID accountId, String key) {
        this.accountId = accountId;
        this.key = key;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProfilePreferenceJpaId that)) {
            return false;
        }
        return Objects.equals(accountId, that.accountId) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, key);
    }
}
