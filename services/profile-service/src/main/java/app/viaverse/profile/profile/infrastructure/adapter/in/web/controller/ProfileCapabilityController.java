package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.DisableIndividualProviderUseCase;
import app.viaverse.profile.profile.application.port.in.EnableIndividualProviderUseCase;
import app.viaverse.profile.profile.application.port.in.GetCurrentProfileUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.EnableIndividualProviderRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.CurrentProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.ProfileDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/capabilities/individual-provider")
public class ProfileCapabilityController {

    private final EnableIndividualProviderUseCase enableIndividualProviderUseCase;
    private final DisableIndividualProviderUseCase disableIndividualProviderUseCase;
    private final GetCurrentProfileUseCase getCurrentProfileUseCase;
    private final ProfileDtoMapper profileDtoMapper;

    public ProfileCapabilityController(
            EnableIndividualProviderUseCase enableIndividualProviderUseCase,
            DisableIndividualProviderUseCase disableIndividualProviderUseCase,
            GetCurrentProfileUseCase getCurrentProfileUseCase,
            ProfileDtoMapper profileDtoMapper
    ) {
        this.enableIndividualProviderUseCase = enableIndividualProviderUseCase;
        this.disableIndividualProviderUseCase = disableIndividualProviderUseCase;
        this.getCurrentProfileUseCase = getCurrentProfileUseCase;
        this.profileDtoMapper = profileDtoMapper;
    }

    @PostMapping("/enable")
    public ApiResponse<CurrentProfileResponse> enable(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EnableIndividualProviderRequest request
    ) {
        UUID accountId = accountId(jwt);
        enableIndividualProviderUseCase.execute(new EnableIndividualProviderUseCase.Command(
                accountId,
                request.acceptedProviderTermsVersion(),
                request.serviceBlurb()
        ));
        return ApiResponse.ok(profileDtoMapper.toCurrentProfileResponse(
                getCurrentProfileUseCase.execute(accountId)
        ));
    }

    @PostMapping("/disable")
    public ApiResponse<CurrentProfileResponse> disable(@AuthenticationPrincipal Jwt jwt) {
        UUID accountId = accountId(jwt);
        disableIndividualProviderUseCase.execute(accountId);
        return ApiResponse.ok(profileDtoMapper.toCurrentProfileResponse(
                getCurrentProfileUseCase.execute(accountId)
        ));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
