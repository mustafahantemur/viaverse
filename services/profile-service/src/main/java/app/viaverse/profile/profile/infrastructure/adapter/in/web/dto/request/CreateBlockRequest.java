package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateBlockRequest(
        @NotNull UUID blockedAccountId,
        @Size(max = 200) String reason
) {
}
