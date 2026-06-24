package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.controller;

import app.viaverse.marketplace.marketplace.application.port.in.AcceptOfferUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.ListOffersForRequestUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.SubmitOfferUseCase;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.request.SubmitOfferRequest;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.JobResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.OfferResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.mapper.MarketplaceDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/requests/{requestId}/offers")
public class OfferController {

    private final SubmitOfferUseCase submitOfferUseCase;
    private final ListOffersForRequestUseCase listOffersForRequestUseCase;
    private final AcceptOfferUseCase acceptOfferUseCase;
    private final MarketplaceDtoMapper mapper;

    public OfferController(
            SubmitOfferUseCase submitOfferUseCase,
            ListOffersForRequestUseCase listOffersForRequestUseCase,
            AcceptOfferUseCase acceptOfferUseCase,
            MarketplaceDtoMapper mapper
    ) {
        this.submitOfferUseCase = submitOfferUseCase;
        this.listOffersForRequestUseCase = listOffersForRequestUseCase;
        this.acceptOfferUseCase = acceptOfferUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ApiResponse<OfferResponse> submit(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SubmitOfferRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(submitOfferUseCase.execute(new SubmitOfferUseCase.Command(
                requestId,
                accountId(jwt),
                request.amountMinor(),
                request.currency(),
                request.message()
        ))));
    }

    @GetMapping
    public ApiResponse<List<OfferResponse>> list(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(listOffersForRequestUseCase.execute(new ListOffersForRequestUseCase.Command(
                        requestId,
                        accountId(jwt)
                )).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @PostMapping("/{offerId}/accept")
    public ApiResponse<JobResponse> accept(
            @PathVariable UUID requestId,
            @PathVariable UUID offerId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(mapper.toResponse(acceptOfferUseCase.execute(new AcceptOfferUseCase.Command(
                requestId,
                offerId,
                accountId(jwt)
        ))));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
