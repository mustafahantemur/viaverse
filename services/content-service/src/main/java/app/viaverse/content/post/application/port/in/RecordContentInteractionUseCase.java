package app.viaverse.content.post.application.port.in;

import app.viaverse.content.post.domain.enums.ContentSignalTypeEnum;
import app.viaverse.content.post.domain.model.ContentInteraction;
import java.time.Instant;
import java.util.UUID;

public interface RecordContentInteractionUseCase {
    ContentInteraction execute(Command command);

    record Command(
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
}
