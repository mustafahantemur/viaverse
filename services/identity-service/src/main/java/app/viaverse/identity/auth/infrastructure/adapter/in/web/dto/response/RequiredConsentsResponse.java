package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.consent.domain.RequiredConsentDocument;
import java.util.List;

/**
 * Response payload for {@code GET /api/v1/auth/required-consents}. Required
 * documents are server-owned and must all be accepted to complete registration;
 * the marketing document is optional and surfaced separately so the client can
 * present it as a distinct opt-in.
 */
public record RequiredConsentsResponse(
        List<RequiredConsentDocument> required,
        RequiredConsentDocument marketing
) {
}
