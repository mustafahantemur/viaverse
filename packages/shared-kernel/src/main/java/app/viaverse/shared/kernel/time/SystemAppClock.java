package app.viaverse.shared.kernel.time;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SystemAppClock implements AppClock {
    private final Clock clock;

    public SystemAppClock() {
        this(Clock.systemUTC());
    }

    public SystemAppClock(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public Instant now() {
        return clock.instant();
    }
}

