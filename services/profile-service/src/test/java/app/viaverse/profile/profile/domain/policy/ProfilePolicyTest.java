package app.viaverse.profile.profile.domain.policy;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.PublicVisibilityEnum;
import app.viaverse.profile.profile.domain.model.Profile;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfilePolicyTest {

    private static final Instant NOW = Instant.parse("2026-05-18T08:00:00Z");

    @Test
    void computesFullScoreWhenEveryBaseSignalExists() {
        Profile profile = new Profile(
                UUID.randomUUID(),
                "Ada Lovelace",
                "Ada",
                "Lovelace",
                UUID.randomUUID(),
                "Mathematician",
                "Writes the first algorithm.",
                "tr-TR",
                "Europe/Istanbul",
                ActiveModeEnum.CUSTOMER,
                0,
                PublicVisibilityEnum.LIMITED,
                NOW,
                NOW,
                0
        );

        assertThat(ProfilePolicy.computeCompleteness(profile, true, true)).isEqualTo(100);
    }

    @Test
    void ignoresBlankOptionalFieldsAndMissingIdentifiers() {
        Profile profile = new Profile(
                UUID.randomUUID(),
                "Ada",
                null,
                null,
                null,
                " ",
                "",
                "tr-TR",
                "Europe/Istanbul",
                ActiveModeEnum.CUSTOMER,
                0,
                PublicVisibilityEnum.LIMITED,
                NOW,
                NOW,
                0
        );

        assertThat(ProfilePolicy.computeCompleteness(profile, false, false)).isEqualTo(30);
    }
}
