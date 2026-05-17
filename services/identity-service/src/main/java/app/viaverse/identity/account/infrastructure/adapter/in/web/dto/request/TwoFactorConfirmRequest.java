package app.viaverse.identity.account.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record TwoFactorConfirmRequest(
        @NotNull UUID flowId,
        @NotBlank @Pattern(regexp = "^\\d{6}$", message = "must be 6 digits") String otp,
        @NotBlank @Pattern(regexp = "^\\d{6}$", message = "must be 6 digits") String totpCode
) {
}
