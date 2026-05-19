package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.response;

import app.viaverse.marketplace.marketplace.domain.enums.JobTimelineEventTypeEnum;
import java.time.Instant;
import java.util.UUID;

public record JobTimelineEntryResponse(
        UUID id,
        UUID jobId,
        UUID actorAccountId,
        JobTimelineEventTypeEnum eventType,
        String message,
        Instant occurredAt,
        Instant createdAt
) {
}
