package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnableIndividualProviderRequest(
        @NotBlank String acceptedProviderTermsVersion,
        @Size(max = 200) String serviceBlurb
) {
}
