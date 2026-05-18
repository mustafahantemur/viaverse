package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateIndividualProviderProfileRequest(
        @Size(max = 200) String serviceBlurb,
        @Size(max = 160) String availabilitySummary,
        boolean acceptsRemote
) {
}
