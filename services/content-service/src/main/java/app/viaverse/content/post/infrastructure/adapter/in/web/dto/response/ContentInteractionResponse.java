package app.viaverse.content.post.infrastructure.adapter.in.web.dto.response;

import app.viaverse.content.post.domain.enums.ContentSignalTypeEnum;
import java.time.Instant;
import java.util.UUID;

public record ContentInteractionResponse(
        UUID id,
        UUID viewerAccountId,
        UUID postId,
        ContentSignalTypeEnum signalType,
        String surface,
        Integer position,
        Long dwellTimeMs,
        UUID sessionId,
        Instant occurredAt
) {
}
