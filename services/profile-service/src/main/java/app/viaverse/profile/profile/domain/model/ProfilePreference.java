package app.viaverse.profile.profile.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * User-owned preference value stored behind a stable key.
 */
public final class ProfilePreference {

    private final UUID accountId;
    private final String key;
    private final String valueJson;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public ProfilePreference(
            UUID accountId,
            String key,
            String valueJson,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.key = requireNonBlank(key, "key");
        this.valueJson = requireNonBlank(valueJson, "valueJson");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getKey() {
        return key;
    }

    public String getValueJson() {
        return valueJson;
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

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
