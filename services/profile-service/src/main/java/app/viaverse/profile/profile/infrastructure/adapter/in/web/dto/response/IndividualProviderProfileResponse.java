package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response;

public record IndividualProviderProfileResponse(
        String serviceBlurb,
        String availabilitySummary,
        boolean acceptsRemote,
        String providerTermsVersionAccepted
) {
}
