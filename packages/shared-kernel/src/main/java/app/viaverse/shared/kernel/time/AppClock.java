package app.viaverse.shared.kernel.time;

import java.time.Instant;

public interface AppClock {
    Instant now();
}

