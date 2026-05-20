package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.CreatePostRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreateCommentRequest;
import app.viaverse.mockwebbff.app.AppDtos.AnnouncementIncidentView;
import app.viaverse.mockwebbff.app.AppDtos.FeedItemView;
import app.viaverse.mockwebbff.app.AppDtos.HashtagSuggestionView;
import app.viaverse.mockwebbff.app.AppDtos.MockPhotoView;
import app.viaverse.mockwebbff.app.AppDtos.PostCommentView;
import app.viaverse.mockwebbff.app.AppDtos.SponsoredAdView;
import app.viaverse.mockwebbff.app.AppDtos.UpdatePostRequest;
import app.viaverse.mockwebbff.app.MockAppService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class FeedController {

    private final MockAppService service;

    public FeedController(MockAppService service) {
        this.service = service;
    }

    @GetMapping("/feed")
    ApiResponse<List<FeedItemView>> feed(@RequestParam(required = false) String type) {
        return ApiResponse.success(service.feed(type));
    }

    @PostMapping("/posts")
    ApiResponse<FeedItemView> createPost(@RequestBody CreatePostRequest request) {
        return ApiResponse.success(service.createPost(request));
    }

    @PatchMapping("/posts/{postId}")
    ApiResponse<FeedItemView> updatePost(@PathVariable String postId, @RequestBody UpdatePostRequest request) {
        return ApiResponse.success(service.updatePost(postId, request));
    }

    @PostMapping("/posts/{postId}/like")
    ApiResponse<FeedItemView> likePost(@PathVariable String postId) {
        return ApiResponse.success(service.likePost(postId));
    }

    @PostMapping("/posts/{postId}/save")
    ApiResponse<FeedItemView> savePost(@PathVariable String postId) {
        return ApiResponse.success(service.savePost(postId));
    }

    @PostMapping("/posts/{postId}/share")
    ApiResponse<FeedItemView> sharePost(@PathVariable String postId) {
        return ApiResponse.success(service.sharePost(postId));
    }

    @GetMapping("/posts/{postId}/comments")
    ApiResponse<List<PostCommentView>> comments(@PathVariable String postId) {
        return ApiResponse.success(service.comments(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    ApiResponse<PostCommentView> createComment(
        @PathVariable String postId,
        @RequestBody CreateCommentRequest request
    ) {
        return ApiResponse.success(service.createComment(postId, request));
    }

    @GetMapping("/feed/hashtags")
    ApiResponse<List<HashtagSuggestionView>> hashtags(@RequestParam(required = false) String query) {
        return ApiResponse.success(service.hashtagSuggestions(query));
    }

    @GetMapping("/media/mock-photos")
    ApiResponse<List<MockPhotoView>> mockPhotos(@RequestParam(required = false) String query) {
        return ApiResponse.success(service.mockPhotos(query));
    }

    @GetMapping("/ads")
    ApiResponse<List<SponsoredAdView>> sponsoredAds(@RequestParam(required = false) String surface) {
        return ApiResponse.success(service.sponsoredAds(surface));
    }

    @GetMapping("/events")
    ApiResponse<List<FeedItemView>> events() {
        return ApiResponse.success(service.events());
    }

    @GetMapping("/announcements")
    ApiResponse<List<FeedItemView>> announcements() {
        return ApiResponse.success(service.announcements());
    }

    @GetMapping("/announcements/incidents")
    ApiResponse<List<AnnouncementIncidentView>> announcementIncidents() {
        return ApiResponse.success(service.announcementIncidents());
    }
}
