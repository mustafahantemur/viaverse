package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.consent.domain.RequiredConsentDocument;
import java.util.List;

/**
 * Public capability-terms payload for flows that happen after registration,
 * such as enabling provider mode or submitting business onboarding.
 */
public record CapabilityTermsResponse(List<RequiredConsentDocument> capabilityTerms) {
}
