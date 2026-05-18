package app.viaverse.media.asset.infrastructure.adapter.in.web.controller;

import app.viaverse.media.asset.application.port.in.CompleteUploadUseCase;
import app.viaverse.media.asset.application.port.in.CreateUploadSessionUseCase;
import app.viaverse.media.asset.application.port.in.ListCurrentMediaAssetsUseCase;
import app.viaverse.media.asset.infrastructure.adapter.in.web.dto.request.CompleteUploadRequest;
import app.viaverse.media.asset.infrastructure.adapter.in.web.dto.request.CreateUploadSessionRequest;
import app.viaverse.media.asset.infrastructure.adapter.in.web.dto.response.MediaAssetResponse;
import app.viaverse.media.asset.infrastructure.adapter.in.web.dto.response.UploadSessionResponse;
import app.viaverse.media.asset.infrastructure.adapter.in.web.mapper.MediaDtoMapper;
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
@RequestMapping("/api/v1")
public class MediaAssetController {

    private final CreateUploadSessionUseCase createUploadSessionUseCase;
    private final CompleteUploadUseCase completeUploadUseCase;
    private final ListCurrentMediaAssetsUseCase listCurrentMediaAssetsUseCase;
    private final MediaDtoMapper mapper;

    public MediaAssetController(
            CreateUploadSessionUseCase createUploadSessionUseCase,
            CompleteUploadUseCase completeUploadUseCase,
            ListCurrentMediaAssetsUseCase listCurrentMediaAssetsUseCase,
            MediaDtoMapper mapper
    ) {
        this.createUploadSessionUseCase = createUploadSessionUseCase;
        this.completeUploadUseCase = completeUploadUseCase;
        this.listCurrentMediaAssetsUseCase = listCurrentMediaAssetsUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/assets/upload-sessions")
    public ApiResponse<UploadSessionResponse> createUploadSession(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateUploadSessionRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(createUploadSessionUseCase.execute(
                new CreateUploadSessionUseCase.Command(
                        accountId(jwt),
                        request.assetKind(),
                        request.contentType(),
                        request.originalFileName()
                )
        )));
    }

    @PostMapping("/assets/{assetId}/complete")
    public ApiResponse<MediaAssetResponse> completeUpload(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID assetId,
            @Valid @RequestBody CompleteUploadRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(completeUploadUseCase.execute(
                new CompleteUploadUseCase.Command(assetId, accountId(jwt), request.checksumSha256())
        )));
    }

    @GetMapping("/me/assets")
    public ApiResponse<List<MediaAssetResponse>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(listCurrentMediaAssetsUseCase.execute(accountId(jwt)).stream()
                .map(mapper::toResponse)
                .toList());
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
