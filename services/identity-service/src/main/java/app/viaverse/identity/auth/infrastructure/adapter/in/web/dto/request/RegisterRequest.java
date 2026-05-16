package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Initial-registration payload. Consent versions are not part of the contract:
 * the server publishes the current required documents via
 * {@code GET /api/v1/auth/required-consents}, the client lists the {@code type}s
 * the user accepted, and the server stamps the canonical version on the stored
 * acceptance record.
 */
public record RegisterRequest(
        @NotBlank String registrationToken,
        @NotBlank String displayName,
        String firstName,
        String lastName,
        @NotEmpty List<ConsentTypeEnum> acceptedRequiredConsents,
        boolean marketingConsentAccepted
) {
}
