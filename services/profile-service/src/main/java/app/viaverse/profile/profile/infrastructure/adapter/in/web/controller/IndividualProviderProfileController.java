package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.GetCurrentIndividualProviderProfileUseCase;
import app.viaverse.profile.profile.application.port.in.UpdateCurrentIndividualProviderProfileUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.UpdateIndividualProviderProfileRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.IndividualProviderProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.IndividualProviderProfileDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/individual-provider-profile")
public class IndividualProviderProfileController {

    private final GetCurrentIndividualProviderProfileUseCase getCurrentUseCase;
    private final UpdateCurrentIndividualProviderProfileUseCase updateCurrentUseCase;
    private final IndividualProviderProfileDtoMapper mapper;

    public IndividualProviderProfileController(
            GetCurrentIndividualProviderProfileUseCase getCurrentUseCase,
            UpdateCurrentIndividualProviderProfileUseCase updateCurrentUseCase,
            IndividualProviderProfileDtoMapper mapper
    ) {
        this.getCurrentUseCase = getCurrentUseCase;
        this.updateCurrentUseCase = updateCurrentUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<IndividualProviderProfileResponse> getCurrent(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(mapper.toResponse(getCurrentUseCase.execute(accountId(jwt))));
    }

    @PatchMapping
    public ApiResponse<IndividualProviderProfileResponse> updateCurrent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateIndividualProviderProfileRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(updateCurrentUseCase.execute(
                new UpdateCurrentIndividualProviderProfileUseCase.Command(
                        accountId(jwt),
                        request.serviceBlurb(),
                        request.availabilitySummary(),
                        request.acceptsRemote(),
                        request.serviceCategories()
                )
        )));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
