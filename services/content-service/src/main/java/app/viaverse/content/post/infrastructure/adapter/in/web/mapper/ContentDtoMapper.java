package app.viaverse.content.post.infrastructure.adapter.in.web.mapper;

import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.content.post.infrastructure.adapter.in.web.dto.response.ContentPostResponse;
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
}
