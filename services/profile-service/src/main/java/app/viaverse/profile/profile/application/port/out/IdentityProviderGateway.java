package app.viaverse.profile.profile.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface IdentityProviderGateway {

    AccountSnapshot getAccountSnapshot(UUID accountId);

    ProviderEnablementFacts getProviderEnablementFacts(UUID accountId);

    void acceptProviderTerms(UUID accountId, String version);

    BusinessEnablementFacts getBusinessEnablementFacts(UUID accountId);

    void acceptBusinessTerms(UUID accountId, String version);

    record AccountSnapshot(
            UUID accountId,
            String displayName,
            String firstName,
            String lastName,
            Instant createdAt
    ) {
    }

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
