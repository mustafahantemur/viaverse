package app.viaverse.identity.account.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.account.application.port.in.GetInternalAccountUseCase;
import app.viaverse.identity.account.application.port.in.GetProviderReadinessUseCase;
import app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response.InternalAccountResponse;
import app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response.ProviderReadinessResponse;
import app.viaverse.identity.shared.security.InternalApiAuthorizer;
import app.viaverse.web.api.ApiResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/accounts")
public class InternalAccountController {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final GetProviderReadinessUseCase getProviderReadinessUseCase;
    private final GetInternalAccountUseCase getInternalAccountUseCase;
    private final InternalApiAuthorizer internalApiAuthorizer;

    public InternalAccountController(
            GetProviderReadinessUseCase getProviderReadinessUseCase,
            GetInternalAccountUseCase getInternalAccountUseCase,
            InternalApiAuthorizer internalApiAuthorizer
    ) {
        this.getProviderReadinessUseCase = getProviderReadinessUseCase;
        this.getInternalAccountUseCase = getInternalAccountUseCase;
        this.internalApiAuthorizer = internalApiAuthorizer;
    }

    @GetMapping("/{accountId}")
    public ApiResponse<InternalAccountResponse> account(
            @PathVariable UUID accountId,
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        GetInternalAccountUseCase.Result result = getInternalAccountUseCase.execute(accountId);
        return ApiResponse.ok(new InternalAccountResponse(
                result.accountId(),
                result.displayName(),
                result.firstName(),
                result.lastName(),
                result.createdAt()
        ));
    }

    @GetMapping("/{accountId}/provider-readiness")
    public ApiResponse<ProviderReadinessResponse> providerReadiness(
            @PathVariable UUID accountId,
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        GetProviderReadinessUseCase.Result result = getProviderReadinessUseCase.execute(accountId);
        return ApiResponse.ok(new ProviderReadinessResponse(
                result.accountId(),
                result.active(),
                result.hasVerifiedIdentifier()
        ));
    }
}
