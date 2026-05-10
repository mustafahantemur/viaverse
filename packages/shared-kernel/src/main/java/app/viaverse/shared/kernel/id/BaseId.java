package app.viaverse.shared.kernel.id;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseId {
    private final UUID value;

    protected BaseId(UUID value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public UUID value() {
        return value;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        BaseId baseId = (BaseId) other;
        return value.equals(baseId.value);
    }

    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

