package app.viaverse.identity.consent.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.consent.application.port.in.AcceptInternalConsentUseCase;
import app.viaverse.identity.consent.application.port.in.GetInternalConsentPolicyUseCase;
import app.viaverse.identity.consent.infrastructure.adapter.in.web.dto.request.AcceptInternalConsentRequest;
import app.viaverse.identity.consent.infrastructure.adapter.in.web.dto.response.InternalConsentPolicyResponse;
import app.viaverse.identity.shared.security.InternalApiAuthorizer;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal")
public class InternalConsentController {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final GetInternalConsentPolicyUseCase getInternalConsentPolicyUseCase;
    private final AcceptInternalConsentUseCase acceptInternalConsentUseCase;
    private final InternalApiAuthorizer internalApiAuthorizer;

    public InternalConsentController(
            GetInternalConsentPolicyUseCase getInternalConsentPolicyUseCase,
            AcceptInternalConsentUseCase acceptInternalConsentUseCase,
            InternalApiAuthorizer internalApiAuthorizer
    ) {
        this.getInternalConsentPolicyUseCase = getInternalConsentPolicyUseCase;
        this.acceptInternalConsentUseCase = acceptInternalConsentUseCase;
        this.internalApiAuthorizer = internalApiAuthorizer;
    }

    @GetMapping("/consent-policy")
    public ApiResponse<InternalConsentPolicyResponse> consentPolicy(
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        return ApiResponse.ok(InternalConsentPolicyResponse.from(getInternalConsentPolicyUseCase.execute()));
    }

    @PostMapping("/accounts/{accountId}/consents")
    public ApiResponse<AcceptInternalConsentUseCase.Result> acceptConsent(
            @PathVariable UUID accountId,
            @Valid @RequestBody AcceptInternalConsentRequest request,
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String internalToken
    ) {
        internalApiAuthorizer.requireAuthorized(internalToken);
        return ApiResponse.ok(acceptInternalConsentUseCase.execute(new AcceptInternalConsentUseCase.Command(
                accountId,
                request.type(),
                request.version(),
                request.source()
        )));
    }
}
