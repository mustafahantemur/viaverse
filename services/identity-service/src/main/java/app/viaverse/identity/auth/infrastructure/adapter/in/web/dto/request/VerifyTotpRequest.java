package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyTotpRequest(
        @NotBlank String partialAuthToken,
        @NotBlank
        @Pattern(regexp = "^\\d{6}$", message = "must be 6 digits")
        String totpCode
) {
}
