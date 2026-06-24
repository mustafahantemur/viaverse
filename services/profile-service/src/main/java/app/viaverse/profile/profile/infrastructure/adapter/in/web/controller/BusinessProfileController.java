package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.GetCurrentBusinessProfileUseCase;
import app.viaverse.profile.profile.application.port.in.StartBusinessOnboardingUseCase;
import app.viaverse.profile.profile.application.port.in.SubmitBusinessOnboardingUseCase;
import app.viaverse.profile.profile.application.port.in.UpdateBusinessDraftUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.SubmitBusinessOnboardingRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.UpdateBusinessDraftRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.BusinessProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.BusinessProfileDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class BusinessProfileController {

    private final StartBusinessOnboardingUseCase startBusinessOnboardingUseCase;
    private final GetCurrentBusinessProfileUseCase getCurrentBusinessProfileUseCase;
    private final UpdateBusinessDraftUseCase updateBusinessDraftUseCase;
    private final SubmitBusinessOnboardingUseCase submitBusinessOnboardingUseCase;
    private final BusinessProfileDtoMapper mapper;

    public BusinessProfileController(
            StartBusinessOnboardingUseCase startBusinessOnboardingUseCase,
            GetCurrentBusinessProfileUseCase getCurrentBusinessProfileUseCase,
            UpdateBusinessDraftUseCase updateBusinessDraftUseCase,
            SubmitBusinessOnboardingUseCase submitBusinessOnboardingUseCase,
            BusinessProfileDtoMapper mapper
    ) {
        this.startBusinessOnboardingUseCase = startBusinessOnboardingUseCase;
        this.getCurrentBusinessProfileUseCase = getCurrentBusinessProfileUseCase;
        this.updateBusinessDraftUseCase = updateBusinessDraftUseCase;
        this.submitBusinessOnboardingUseCase = submitBusinessOnboardingUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/capabilities/business/start")
    public ApiResponse<BusinessProfileResponse> start(@AuthenticationPrincipal Jwt jwt) {
        UUID accountId = accountId(jwt);
        startBusinessOnboardingUseCase.execute(accountId);
        return ApiResponse.ok(mapper.toResponse(getCurrentBusinessProfileUseCase.execute(accountId)));
    }

    @GetMapping("/business")
    public ApiResponse<BusinessProfileResponse> current(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(mapper.toResponse(getCurrentBusinessProfileUseCase.execute(accountId(jwt))));
    }

    @PatchMapping("/business/draft")
    public ApiResponse<BusinessProfileResponse> updateDraft(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateBusinessDraftRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(updateBusinessDraftUseCase.execute(
                new UpdateBusinessDraftUseCase.Command(
                        accountId(jwt),
                        request.legalName(),
                        request.tradeName(),
                        request.sector(),
                        request.taxId(),
                        request.addressLine(),
                        request.district(),
                        request.city(),
                        request.country(),
                        request.phone(),
                        request.emailPublic(),
                        request.logoMediaId(),
                        request.openingHoursJson(),
                        request.serviceCategories()
                )
        )));
    }

    @PostMapping("/capabilities/business/submit")
    public ApiResponse<BusinessProfileResponse> submit(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SubmitBusinessOnboardingRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(submitBusinessOnboardingUseCase.execute(
                new SubmitBusinessOnboardingUseCase.Command(
                        accountId(jwt),
                        request.acceptedBusinessTermsVersion()
                )
        )));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
