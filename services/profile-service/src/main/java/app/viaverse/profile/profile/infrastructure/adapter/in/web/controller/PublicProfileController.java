package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.GetPublicProfileUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.PublicProfileResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.ProfileDtoMapper;
import app.viaverse.web.api.ApiResponse;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
public class PublicProfileController {

    private final GetPublicProfileUseCase getPublicProfileUseCase;
    private final ProfileDtoMapper profileDtoMapper;

    public PublicProfileController(
            GetPublicProfileUseCase getPublicProfileUseCase,
            ProfileDtoMapper profileDtoMapper
    ) {
        this.getPublicProfileUseCase = getPublicProfileUseCase;
        this.profileDtoMapper = profileDtoMapper;
    }

    @GetMapping("/{accountId}")
    public ApiResponse<PublicProfileResponse> getPublic(
            @PathVariable UUID accountId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID viewerAccountId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return ApiResponse.ok(profileDtoMapper.toPublicProfileResponse(
                getPublicProfileUseCase.execute(new GetPublicProfileUseCase.Command(accountId, viewerAccountId))
        ));
    }
}
