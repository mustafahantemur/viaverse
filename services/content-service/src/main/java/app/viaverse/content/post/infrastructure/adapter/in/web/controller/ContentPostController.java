package app.viaverse.content.post.infrastructure.adapter.in.web.controller;

import app.viaverse.content.post.application.port.in.CreateContentPostUseCase;
import app.viaverse.content.post.application.port.in.ListCurrentContentPostsUseCase;
import app.viaverse.content.post.application.port.in.ListPublishedContentPostsUseCase;
import app.viaverse.content.post.infrastructure.adapter.in.web.dto.request.CreateContentPostRequest;
import app.viaverse.content.post.infrastructure.adapter.in.web.dto.response.ContentPostResponse;
import app.viaverse.content.post.infrastructure.adapter.in.web.mapper.ContentDtoMapper;
import app.viaverse.web.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ContentPostController {

    private final CreateContentPostUseCase createContentPostUseCase;
    private final ListPublishedContentPostsUseCase listPublishedContentPostsUseCase;
    private final ListCurrentContentPostsUseCase listCurrentContentPostsUseCase;
    private final ContentDtoMapper mapper;

    public ContentPostController(
            CreateContentPostUseCase createContentPostUseCase,
            ListPublishedContentPostsUseCase listPublishedContentPostsUseCase,
            ListCurrentContentPostsUseCase listCurrentContentPostsUseCase,
            ContentDtoMapper mapper
    ) {
        this.createContentPostUseCase = createContentPostUseCase;
        this.listPublishedContentPostsUseCase = listPublishedContentPostsUseCase;
        this.listCurrentContentPostsUseCase = listCurrentContentPostsUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/posts")
    public ApiResponse<ContentPostResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateContentPostRequest request
    ) {
        return ApiResponse.ok(mapper.toResponse(createContentPostUseCase.execute(
                new CreateContentPostUseCase.Command(
                        UUID.fromString(jwt.getSubject()),
                        request.authorMode(),
                        request.postType(),
                        request.title(),
                        request.body(),
                        request.city(),
                        request.district(),
                        request.eventStartsAt(),
                        request.eventEndsAt(),
                        request.mediaAssetIds() == null ? List.of() : request.mediaAssetIds()
                )
        )));
    }

    @GetMapping("/posts/published")
    public ApiResponse<List<ContentPostResponse>> published(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district
    ) {
        return ApiResponse.ok(listPublishedContentPostsUseCase.execute(city, district).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/me/posts")
    public ApiResponse<List<ContentPostResponse>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(listCurrentContentPostsUseCase.execute(UUID.fromString(jwt.getSubject())).stream()
                .map(mapper::toResponse)
                .toList());
    }
}
