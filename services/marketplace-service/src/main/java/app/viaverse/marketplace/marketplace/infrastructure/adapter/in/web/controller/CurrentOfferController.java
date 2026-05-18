package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.controller;

import app.viaverse.marketplace.marketplace.application.port.in.ListCurrentOffersUseCase;
import app.viaverse.marketplace.marketplace.application.port.in.WithdrawOfferUseCase;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response.OfferResponse;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.mapper.MarketplaceDtoMapper;
import app.viaverse.web.api.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CurrentOfferController {
    private final ListCurrentOffersUseCase listCurrentOffersUseCase;
    private final WithdrawOfferUseCase withdrawOfferUseCase;
    private final MarketplaceDtoMapper mapper;

    public CurrentOfferController(
            ListCurrentOffersUseCase listCurrentOffersUseCase,
            WithdrawOfferUseCase withdrawOfferUseCase,
            MarketplaceDtoMapper mapper
    ) {
        this.listCurrentOffersUseCase = listCurrentOffersUseCase;
        this.withdrawOfferUseCase = withdrawOfferUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/me/offers")
    public ApiResponse<List<OfferResponse>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(listCurrentOffersUseCase.execute(accountId(jwt)).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @PostMapping("/offers/{offerId}/withdraw")
    public ApiResponse<OfferResponse> withdraw(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID offerId
    ) {
        return ApiResponse.ok(mapper.toResponse(withdrawOfferUseCase.execute(
                new WithdrawOfferUseCase.Command(offerId, accountId(jwt))
        )));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
