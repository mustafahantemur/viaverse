package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RegisterRequest(
        @NotBlank String registrationToken,
        @NotBlank String displayName,
        String firstName,
        String lastName,
        @NotEmpty List<@Valid ConsentRequest> requiredConsents,
        boolean marketingConsentAccepted
) {
    public record ConsentRequest(@NotNull ConsentTypeEnum type, @NotBlank String version) {
    }
}
