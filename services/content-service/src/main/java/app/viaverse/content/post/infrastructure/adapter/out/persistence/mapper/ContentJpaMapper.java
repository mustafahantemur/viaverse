package app.viaverse.content.post.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.content.post.domain.model.ContentInteraction;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.entity.ContentInteractionJpaEntity;
import app.viaverse.content.post.infrastructure.adapter.out.persistence.entity.ContentPostJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ContentJpaMapper {
    public ContentPostJpaEntity toEntity(ContentPost post) {
        return new ContentPostJpaEntity(
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
                post.getUpdatedAt(),
                post.getVersion()
        );
    }

    public ContentPost toDomain(ContentPostJpaEntity entity) {
        return new ContentPost(
                entity.getId(),
                entity.getAuthorAccountId(),
                entity.getAuthorMode(),
                entity.getPostType(),
                entity.getTitle(),
                entity.getBody(),
                entity.getCity(),
                entity.getDistrict(),
                entity.getEventStartsAt(),
                entity.getEventEndsAt(),
                entity.getMediaAssetIds(),
                entity.getStatus(),
                entity.getModerationStatus(),
                entity.getPublishedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public ContentInteractionJpaEntity toEntity(ContentInteraction interaction) {
        return new ContentInteractionJpaEntity(
                interaction.getId(),
                interaction.getViewerAccountId(),
                interaction.getPostId(),
                interaction.getSignalType(),
                interaction.getSurface(),
                interaction.getPosition(),
                interaction.getDwellTimeMs(),
                interaction.getSessionId(),
                interaction.getOccurredAt(),
                interaction.getCreatedAt(),
                interaction.getUpdatedAt(),
                interaction.getVersion()
        );
    }

    public ContentInteraction toDomain(ContentInteractionJpaEntity entity) {
        return new ContentInteraction(
                entity.getId(),
                entity.getViewerAccountId(),
                entity.getPostId(),
                entity.getSignalType(),
                entity.getSurface(),
                entity.getPosition(),
                entity.getDwellTimeMs(),
                entity.getSessionId(),
                entity.getOccurredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
