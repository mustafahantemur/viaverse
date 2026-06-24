package app.viaverse.mockwebbff.app.seed;

import java.time.Instant;

final class SeedClock {

    private SeedClock() {
    }

    static String minutesAgo(long minutes) {
        return Instant.now().minusSeconds(minutes * 60).toString();
    }
}
