package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.GetCurrentProfileUseCase;
import app.viaverse.profile.profile.application.port.in.UpdateCurrentActiveModeUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.UpdateActiveModeRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.CurrentProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.ProfileDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/active-mode")
public class ActiveModeController {

    private final UpdateCurrentActiveModeUseCase updateCurrentActiveModeUseCase;
    private final GetCurrentProfileUseCase getCurrentProfileUseCase;
    private final ProfileDtoMapper profileDtoMapper;

    public ActiveModeController(
            UpdateCurrentActiveModeUseCase updateCurrentActiveModeUseCase,
            GetCurrentProfileUseCase getCurrentProfileUseCase,
            ProfileDtoMapper profileDtoMapper
    ) {
        this.updateCurrentActiveModeUseCase = updateCurrentActiveModeUseCase;
        this.getCurrentProfileUseCase = getCurrentProfileUseCase;
        this.profileDtoMapper = profileDtoMapper;
    }

    @PatchMapping
    public ApiResponse<CurrentProfileResponse> updateCurrent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateActiveModeRequest request
    ) {
        UUID accountId = UUID.fromString(jwt.getSubject());
        updateCurrentActiveModeUseCase.execute(new UpdateCurrentActiveModeUseCase.Command(
                accountId,
                request.activeMode()
        ));
        return ApiResponse.ok(profileDtoMapper.toCurrentProfileResponse(
                getCurrentProfileUseCase.execute(accountId)
        ));
    }
}
