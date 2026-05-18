package app.viaverse.profile.profile.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileBlockTest {

    private static final Instant NOW = Instant.parse("2026-05-18T08:00:00Z");

    @Test
    void cannotBlockSelf() {
        UUID accountId = UUID.randomUUID();

        assertThatThrownBy(() -> new ProfileBlock(accountId, accountId, null, NOW, NOW, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot block itself");
    }
}
