package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Initial-registration payload.
 *
 * <p>{@code password} is required when the registration token was issued via
 * OTP (email / phone signup) and optional when the token came from a verified
 * social IdP — Google / Apple already proved identifier ownership, the user
 * can add a password later via {@code POST /me/password} if they want a
 * fallback credential. The server enforces this asymmetry; clients always
 * send the field and the server ignores it on the social path if absent.
 *
 * <p>{@code acceptedRequiredConsents} carries only the consent {@code type}s
 * the user has accepted. The current document version is resolved on the
 * server (see {@code GET /api/v1/auth/required-consents}).
 */
public record RegisterRequest(
        @NotBlank String registrationToken,
        @NotBlank String displayName,
        String firstName,
        String lastName,
        String password,
        @NotEmpty List<ConsentTypeEnum> acceptedRequiredConsents,
        boolean marketingConsentAccepted
) {
}
