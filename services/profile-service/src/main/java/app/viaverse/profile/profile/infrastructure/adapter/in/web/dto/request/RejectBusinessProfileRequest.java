package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectBusinessProfileRequest(@NotBlank @Size(max = 240) String reason) {
}
