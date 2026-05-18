package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.controller;

import app.viaverse.marketplace.marketplace.application.port.in.CreateServiceRequestUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.ListCurrentServiceRequestsUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.ListOpenServiceRequestsUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.ListRelevantServiceRequestsUseCase;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.request.CreateServiceRequestRequest;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.ServiceRequestResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.mapper.MarketplaceDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ServiceRequestController {

    private final CreateServiceRequestUseCase createServiceRequestUseCase;
    private final ListOpenServiceRequestsUseCase listOpenServiceRequestsUseCase;
    private final ListRelevantServiceRequestsUseCase listRelevantServiceRequestsUseCase;
    private final ListCurrentServiceRequestsUseCase listCurrentServiceRequestsUseCase;
    private final MarketplaceDtoMapper mapper;

    public ServiceRequestController(
            CreateServiceRequestUseCase createServiceRequestUseCase,
            ListOpenServiceRequestsUseCase listOpenServiceRequestsUseCase,
            ListRelevantServiceRequestsUseCase listRelevantServiceRequestsUseCase,
            ListCurrentServiceRequestsUseCase listCurrentServiceRequestsUseCase,
            MarketplaceDtoMapper mapper
    ) {
        this.createServiceRequestUseCase = createServiceRequestUseCase;
        this.listOpenServiceRequestsUseCase = listOpenServiceRequestsUseCase;
        this.listRelevantServiceRequestsUseCase = listRelevantServiceRequestsUseCase;
        this.listCurrentServiceRequestsUseCase = listCurrentServiceRequestsUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/requests")
    public ApiResponse<ServiceRequestResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateServiceRequestRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(createServiceRequestUseCase.execute(
                new CreateServiceRequestUseCase.Command(
                        accountId(jwt),
                        request.title(),
                        request.description(),
                        request.category(),
                        request.budgetMinAmountMinor(),
                        request.budgetMaxAmountMinor(),
                        request.currency() == null ? "TRY" : request.currency(),
                        request.remoteAllowed(),
                        request.district(),
                        request.city(),
                        request.mediaAssetIds() == null ? List.of() : request.mediaAssetIds()
                )
        )));
    }

    @GetMapping("/requests/open")
    public ApiResponse<List<ServiceRequestResponse>> open() {
        return ApiResponse.ok(listOpenServiceRequestsUseCase.execute().stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/feed/work")
    public ApiResponse<List<ServiceRequestResponse>> workFeed(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(listRelevantServiceRequestsUseCase.execute(accountId(jwt)).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/me/requests")
    public ApiResponse<List<ServiceRequestResponse>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(listCurrentServiceRequestsUseCase.execute(accountId(jwt)).stream()
                .map(mapper::toResponse)
                .toList());
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
