package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.BlockProfileUseCase;
import app.viaverse.profile.profile.application.port.in.ListCurrentBlocksUseCase;
import app.viaverse.profile.profile.application.port.in.UnblockProfileUseCase;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request.CreateBlockRequest;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response.ProfileBlockResponse;
import app.viaverse.profile.profile.infrastructure.adapter.in.web.mapper.ProfileDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/blocks")
public class ProfileBlockController {

    private final ListCurrentBlocksUseCase listCurrentBlocksUseCase;
    private final BlockProfileUseCase blockProfileUseCase;
    private final UnblockProfileUseCase unblockProfileUseCase;
    private final ProfileDtoMapper profileDtoMapper;

    public ProfileBlockController(
            ListCurrentBlocksUseCase listCurrentBlocksUseCase,
            BlockProfileUseCase blockProfileUseCase,
            UnblockProfileUseCase unblockProfileUseCase,
            ProfileDtoMapper profileDtoMapper
    ) {
        this.listCurrentBlocksUseCase = listCurrentBlocksUseCase;
        this.blockProfileUseCase = blockProfileUseCase;
        this.unblockProfileUseCase = unblockProfileUseCase;
        this.profileDtoMapper = profileDtoMapper;
    }

    @GetMapping
    public ApiResponse<List<ProfileBlockResponse>> listCurrent(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(listCurrentBlocksUseCase.execute(accountId(jwt)).stream()
                .map(profileDtoMapper::toProfileBlockResponse)
                .toList());
    }

    @PostMapping
    public ApiResponse<ProfileBlockResponse> block(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateBlockRequest request
    ) {
        return ApiResponse.ok(profileDtoMapper.toProfileBlockResponse(blockProfileUseCase.execute(
                new BlockProfileUseCase.Command(accountId(jwt), request.blockedAccountId(), request.reason())
        )));
    }

    @DeleteMapping("/{blockedAccountId}")
    public ApiResponse<Void> unblock(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID blockedAccountId
    ) {
        unblockProfileUseCase.execute(new UnblockProfileUseCase.Command(accountId(jwt), blockedAccountId));
        return ApiResponse.ok(null);
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
