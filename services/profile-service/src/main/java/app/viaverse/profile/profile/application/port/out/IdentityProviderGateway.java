package app.viaverse.profile.profile.application.port.out;

import java.util.UUID;

public interface IdentityProviderGateway {

    ProviderEnablementFacts getProviderEnablementFacts(UUID accountId);

    void acceptProviderTerms(UUID accountId, String version);

    BusinessEnablementFacts getBusinessEnablementFacts(UUID accountId);

    void acceptBusinessTerms(UUID accountId, String version);

    record ProviderEnablementFacts(
            boolean active,
            boolean hasVerifiedIdentifier,
            String currentProviderTermsVersion
    ) {
    }

    record BusinessEnablementFacts(
            boolean active,
            boolean hasVerifiedIdentifier,
            String currentBusinessTermsVersion
    ) {
    }
}
