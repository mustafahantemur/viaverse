package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordLoginRequest(
        @NotBlank String identifier,
        @NotBlank String password
) {
}
