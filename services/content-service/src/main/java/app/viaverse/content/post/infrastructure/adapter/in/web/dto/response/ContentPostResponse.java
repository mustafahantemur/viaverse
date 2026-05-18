package app.viaverse.content.post.infrastructure.adapter.in.web.dto.response;

import app.viaverse.content.post.domain.enums.ContentAuthorModeEnum;
import app.viaverse.content.post.domain.enums.ContentModerationStatusEnum;
import app.viaverse.content.post.domain.enums.ContentPostStatusEnum;
import app.viaverse.content.post.domain.enums.ContentPostTypeEnum;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ContentPostResponse(
        UUID id,
        UUID authorAccountId,
        ContentAuthorModeEnum authorMode,
        ContentPostTypeEnum postType,
        String title,
        String body,
        String city,
        String district,
        Instant eventStartsAt,
        Instant eventEndsAt,
        List<UUID> mediaAssetIds,
        ContentPostStatusEnum status,
        ContentModerationStatusEnum moderationStatus,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
