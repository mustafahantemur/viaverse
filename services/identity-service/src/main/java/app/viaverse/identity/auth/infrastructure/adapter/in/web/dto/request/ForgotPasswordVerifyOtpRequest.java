package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ForgotPasswordVerifyOtpRequest(
        @NotNull UUID flowId,
        @NotBlank String otp
) {
}
