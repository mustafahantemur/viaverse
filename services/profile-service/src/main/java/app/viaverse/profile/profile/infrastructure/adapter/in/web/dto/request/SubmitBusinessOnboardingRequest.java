package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubmitBusinessOnboardingRequest(@NotBlank String acceptedBusinessTermsVersion) {
}
