package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Initial-registration submission. Phone is optional; if provided it goes
 * through libphonenumber on the server. {@code acceptedRequiredConsents}
 * carries only the consent types — the server stamps the canonical version.
 */
public record StartRegistrationRequest(
        @NotBlank @Email String email,
        String phone,
        @NotBlank String displayName,
        String firstName,
        String lastName,
        @NotBlank String password,
        @NotEmpty List<ConsentTypeEnum> acceptedRequiredConsents,
        boolean marketingConsentAccepted
) {
}
