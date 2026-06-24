package app.viaverse.content.post.infrastructure.adapter.in.web.mapper;

import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.content.post.domain.model.ContentInteraction;
import app.viaverse.content.post.application.model.SocialFeedItem;
import app.viaverse.content.post.infrastructure.adapter.in.web.dto.response.ContentInteractionResponse;
import app.viaverse.content.post.infrastructure.adapter.in.web.dto.response.ContentPostResponse;
import app.viaverse.content.post.infrastructure.adapter.in.web.dto.response.SocialFeedItemResponse;
import org.springframework.stereotype.Component;

@Component
public class ContentDtoMapper {
    public ContentPostResponse toResponse(ContentPost post) {
        return new ContentPostResponse(
                post.getId(),
                post.getAuthorAccountId(),
                post.getAuthorMode(),
                post.getPostType(),
                post.getTitle(),
                post.getBody(),
                post.getCity(),
                post.getDistrict(),
                post.getEventStartsAt(),
                post.getEventEndsAt(),
                post.getMediaAssetIds(),
                post.getStatus(),
                post.getModerationStatus(),
                post.getPublishedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public ContentInteractionResponse toResponse(ContentInteraction interaction) {
        return new ContentInteractionResponse(
                interaction.getId(),
                interaction.getViewerAccountId(),
                interaction.getPostId(),
                interaction.getSignalType(),
                interaction.getSurface(),
                interaction.getPosition(),
                interaction.getDwellTimeMs(),
                interaction.getSessionId(),
                interaction.getOccurredAt()
        );
    }

    public SocialFeedItemResponse toResponse(SocialFeedItem item) {
        return new SocialFeedItemResponse(toResponse(item.post()), item.score(), item.reason());
    }
}
