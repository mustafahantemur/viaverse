package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record VerifyRegistrationOtpRequest(
        @NotNull UUID draftId,
        @NotBlank @Pattern(regexp = "^\\d{6}$", message = "must be 6 digits") String otp
) {
}
