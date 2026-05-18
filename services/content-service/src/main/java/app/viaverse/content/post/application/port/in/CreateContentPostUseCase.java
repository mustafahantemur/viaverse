package app.viaverse.content.post.application.port.in;

import app.viaverse.content.post.domain.enums.ContentAuthorModeEnum;
import app.viaverse.content.post.domain.enums.ContentPostTypeEnum;
import app.viaverse.content.post.domain.model.ContentPost;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CreateContentPostUseCase {
    ContentPost execute(Command command);

    record Command(
            UUID authorAccountId,
            ContentAuthorModeEnum authorMode,
            ContentPostTypeEnum postType,
            String title,
            String body,
            String city,
            String district,
            Instant eventStartsAt,
            Instant eventEndsAt,
            List<UUID> mediaAssetIds
    ) {
    }
}
