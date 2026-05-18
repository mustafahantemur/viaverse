package app.viaverse.profile.profile.infrastructure.adapter.out.identity;

import app.viaverse.profile.config.ProfileIdentityProperties;
import app.viaverse.profile.profile.application.port.out.IdentityProviderGateway;
import app.viaverse.shared.kernel.error.TechnicalException;
import app.viaverse.web.api.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class IdentityProviderHttpAdapter implements IdentityProviderGateway {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final ParameterizedTypeReference<ApiResponse<ProviderReadinessResponse>> READINESS_BODY =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<ApiResponse<ConsentPolicyResponse>> POLICY_BODY =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final ProfileIdentityProperties properties;

    public IdentityProviderHttpAdapter(
            @Qualifier("identityInternalRestClient") RestClient restClient,
            ProfileIdentityProperties properties
    ) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public ProviderEnablementFacts getProviderEnablementFacts(UUID accountId) {
        try {
            ApiResponse<ProviderReadinessResponse> readiness = restClient.get()
                    .uri("/api/v1/internal/accounts/{accountId}/provider-readiness", accountId)
                    .header(INTERNAL_TOKEN_HEADER, properties.getInternalApiToken())
                    .retrieve()
                    .body(READINESS_BODY);
            ApiResponse<ConsentPolicyResponse> policy = restClient.get()
                    .uri("/api/v1/internal/consent-policy")
                    .header(INTERNAL_TOKEN_HEADER, properties.getInternalApiToken())
                    .retrieve()
                    .body(POLICY_BODY);
            ProviderReadinessResponse readinessData = requireData(readiness);
            ConsentPolicyDocument providerTerms = requireData(policy).capabilityTerms().stream()
                    .filter(document -> "PROVIDER_TERMS".equals(document.type()))
                    .findFirst()
                    .orElseThrow(() -> unavailable("Identity consent policy omitted provider terms"));
            return new ProviderEnablementFacts(
                    readinessData.active(),
                    readinessData.hasVerifiedIdentifier(),
                    providerTerms.version()
            );
        } catch (HttpStatusCodeException | ResourceAccessException exception) {
            throw unavailable("Identity provider readiness is unavailable", exception);
        }
    }

    @Override
    public void acceptProviderTerms(UUID accountId, String version) {
        acceptCapabilityTerms(accountId, "PROVIDER_TERMS", version);
    }

    @Override
    public BusinessEnablementFacts getBusinessEnablementFacts(UUID accountId) {
        try {
            ApiResponse<ProviderReadinessResponse> readiness = restClient.get()
                    .uri("/api/v1/internal/accounts/{accountId}/provider-readiness", accountId)
                    .header(INTERNAL_TOKEN_HEADER, properties.getInternalApiToken())
                    .retrieve()
                    .body(READINESS_BODY);
            ApiResponse<ConsentPolicyResponse> policy = restClient.get()
                    .uri("/api/v1/internal/consent-policy")
                    .header(INTERNAL_TOKEN_HEADER, properties.getInternalApiToken())
                    .retrieve()
                    .body(POLICY_BODY);
            ProviderReadinessResponse readinessData = requireData(readiness);
            ConsentPolicyDocument businessTerms = requireData(policy).capabilityTerms().stream()
                    .filter(document -> "BUSINESS_TERMS".equals(document.type()))
                    .findFirst()
                    .orElseThrow(() -> unavailable("Identity consent policy omitted business terms"));
            return new BusinessEnablementFacts(
                    readinessData.active(),
                    readinessData.hasVerifiedIdentifier(),
                    businessTerms.version()
            );
        } catch (HttpStatusCodeException | ResourceAccessException exception) {
            throw unavailable("Identity business readiness is unavailable", exception);
        }
    }

    @Override
    public void acceptBusinessTerms(UUID accountId, String version) {
        acceptCapabilityTerms(accountId, "BUSINESS_TERMS", version);
    }

    private void acceptCapabilityTerms(UUID accountId, String type, String version) {
        try {
            restClient.post()
                    .uri("/api/v1/internal/accounts/{accountId}/consents", accountId)
                    .header(INTERNAL_TOKEN_HEADER, properties.getInternalApiToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AcceptConsentRequest(type, version, "profile-service"))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpStatusCodeException | ResourceAccessException exception) {
            throw unavailable("Identity consent stamping is unavailable", exception);
        }
    }

    private <T> T requireData(ApiResponse<T> response) {
        if (response == null || !response.success() || response.data() == null) {
            throw unavailable("Identity internal API returned an invalid response");
        }
        return response.data();
    }

    private TechnicalException unavailable(String message) {
        return new TechnicalException(app.viaverse.shared.kernel.error.AppErrorCode.TECHNICAL_ERROR, message);
    }

    private TechnicalException unavailable(String message, Throwable cause) {
        return new TechnicalException(app.viaverse.shared.kernel.error.AppErrorCode.TECHNICAL_ERROR, message, cause);
    }

    private record ProviderReadinessResponse(
            UUID accountId,
            boolean active,
            boolean hasVerifiedIdentifier
    ) {
    }

    private record ConsentPolicyResponse(List<ConsentPolicyDocument> capabilityTerms) {
    }

    private record ConsentPolicyDocument(String type, String category, String version, String url) {
    }

    private record AcceptConsentRequest(String type, String version, String source) {
    }
}
