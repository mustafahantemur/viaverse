package app.viaverse.identity.auth.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StartAuthRequest(@NotBlank String identifier) {
}
