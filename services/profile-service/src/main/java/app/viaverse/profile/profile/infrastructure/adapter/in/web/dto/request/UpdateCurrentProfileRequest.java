package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import app.viaverse.profile.profile.domain.enums.PublicVisibilityEnum;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateCurrentProfileRequest(
        @Size(min = 1, max = 120) String displayName,
        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,
        UUID avatarMediaId,
        @Size(max = 80) String headline,
        @Size(max = 600) String bio,
        @Size(min = 1, max = 32) String locale,
        @Size(min = 1, max = 64) String timezone,
        PublicVisibilityEnum publicVisibility
) {
}
