package app.viaverse.content.post.application.model;

import app.viaverse.content.post.domain.model.ContentPost;

public record SocialFeedItem(
        ContentPost post,
        int score,
        String reason
) {
}
