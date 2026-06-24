package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.config.ProfileInternalApiAuthorizer;
import app.viaverse.profile.profile.application.port.in.GetMarketplaceEligibilityUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.MarketplaceEligibilityResponse;
import app.viaverse.web.api.ApiResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/profiles")
public class InternalProfileEligibilityController {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final GetMarketplaceEligibilityUseCase useCase;
    private final ProfileInternalApiAuthorizer internalApiAuthorizer;

    public InternalProfileEligibilityController(
            GetMarketplaceEligibilityUseCase useCase,
            ProfileInternalApiAuthorizer internalApiAuthorizer
    ) {
        this.useCase = useCase;
        this.internalApiAuthorizer = internalApiAuthorizer;
    }

    @GetMapping("/{accountId}/marketplace-eligibility")
    public ApiResponse<MarketplaceEligibilityResponse> getEligibility(
            @PathVariable UUID accountId,
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        var result = useCase.execute(accountId);
        return ApiResponse.ok(new MarketplaceEligibilityResponse(
                result.accountId(),
                result.canOffer(),
                result.activeMode(),
                result.individualProviderEnabled(),
                result.businessEnabled(),
                result.businessVerificationStatus(),
                result.individualProviderServiceCategories(),
                result.businessServiceCategories(),
                result.individualProviderAcceptsRemote(),
                result.businessDistrict(),
                result.businessCity()
        ));
    }
}
