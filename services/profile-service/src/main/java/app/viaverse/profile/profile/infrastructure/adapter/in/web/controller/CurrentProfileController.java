package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.GetCurrentProfileUseCase;
import app.viaverse.profile.profile.application.port.in.UpdateCurrentProfileUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.UpdateCurrentProfileRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.CurrentProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.ProfileDtoMapper;
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
@RequestMapping("/api/v1/me/profile")
public class CurrentProfileController {

    private final GetCurrentProfileUseCase getCurrentProfileUseCase;
    private final UpdateCurrentProfileUseCase updateCurrentProfileUseCase;
    private final ProfileDtoMapper profileDtoMapper;

    public CurrentProfileController(
            GetCurrentProfileUseCase getCurrentProfileUseCase,
            UpdateCurrentProfileUseCase updateCurrentProfileUseCase,
            ProfileDtoMapper profileDtoMapper
    ) {
        this.getCurrentProfileUseCase = getCurrentProfileUseCase;
        this.updateCurrentProfileUseCase = updateCurrentProfileUseCase;
        this.profileDtoMapper = profileDtoMapper;
    }

    @GetMapping
    public ApiResponse<CurrentProfileResponse> getCurrent(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(profileDtoMapper.toCurrentProfileResponse(
                getCurrentProfileUseCase.execute(accountId(jwt))
        ));
    }

    @PatchMapping
    public ApiResponse<CurrentProfileResponse> updateCurrent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateCurrentProfileRequest request
    ) {
        UUID accountId = accountId(jwt);
        updateCurrentProfileUseCase.execute(new UpdateCurrentProfileUseCase.Command(
                        accountId,
                        request.displayName(),
                        request.firstName(),
                        request.lastName(),
                        request.avatarMediaId(),
                        request.headline(),
                        request.bio(),
                        request.locale(),
                        request.timezone(),
                        request.publicVisibility()
                ));
        return ApiResponse.ok(profileDtoMapper.toCurrentProfileResponse(
                getCurrentProfileUseCase.execute(accountId)
        ));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
