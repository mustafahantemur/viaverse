package app.viaverse.content.post.infrastructure.adapter.in.web.dto.response;

public record SocialFeedItemResponse(
        ContentPostResponse post,
        int score,
        String reason
) {
}
