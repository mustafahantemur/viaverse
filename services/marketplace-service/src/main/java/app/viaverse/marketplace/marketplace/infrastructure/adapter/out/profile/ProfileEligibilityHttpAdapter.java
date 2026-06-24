package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.profile;

import app.viaverse.marketplace.config.MarketplaceProfileProperties;
import app.viaverse.marketplace.marketplace.application.port.out.ProviderEligibilityGateway;
import app.viaverse.shared.kernel.error.TechnicalException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ProfileEligibilityHttpAdapter implements ProviderEligibilityGateway {

    private static final ParameterizedTypeReference<ApiEnvelope<EligibilityResponse>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient profileRestClient;
    private final MarketplaceProfileProperties properties;

    public ProfileEligibilityHttpAdapter(
            @Qualifier("profileInternalRestClient") RestClient profileRestClient,
            MarketplaceProfileProperties properties
    ) {
        this.profileRestClient = profileRestClient;
        this.properties = properties;
    }

    @Override
    public Eligibility getEligibility(UUID accountId) {
        try {
            EligibilityResponse response = profileRestClient
                    .get()
                    .uri("/api/v1/internal/profiles/{accountId}/marketplace-eligibility", accountId)
                    .header("X-Internal-Token", properties.getInternalApiToken())
                    .retrieve()
                    .body(RESPONSE_TYPE)
                    .data();
            return new Eligibility(
                    response.accountId(),
                    response.canOffer(),
                    response.activeMode(),
                    response.individualProviderEnabled(),
                    response.businessEnabled(),
                    response.businessVerificationStatus(),
                    response.individualProviderServiceCategories(),
                    response.businessServiceCategories(),
                    response.individualProviderAcceptsRemote(),
                    response.businessDistrict(),
                    response.businessCity()
            );
        } catch (RestClientException exception) {
            throw new TechnicalException("Failed to read marketplace eligibility from profile-service", exception);
        }
    }

    private record ApiEnvelope<T>(boolean success, T data, Object error) {
    }

    private record EligibilityResponse(
            UUID accountId,
            boolean canOffer,
            String activeMode,
            boolean individualProviderEnabled,
            boolean businessEnabled,
            String businessVerificationStatus,
            java.util.Set<app.viaverse.contracts.marketplace.MarketplaceServiceCategory> individualProviderServiceCategories,
            java.util.Set<app.viaverse.contracts.marketplace.MarketplaceServiceCategory> businessServiceCategories,
            boolean individualProviderAcceptsRemote,
            String businessDistrict,
            String businessCity
    ) {
    }
}
