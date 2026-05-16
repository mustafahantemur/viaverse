package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AdminRegisterRequest(
        @NotBlank String invitationToken,
        @NotBlank String registrationToken,
        @NotBlank String displayName,
        String firstName,
        String lastName,
        @NotEmpty @Valid List<RegisterRequest.ConsentRequest> requiredConsents,
        boolean marketingConsentAccepted
) {
}
