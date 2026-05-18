package app.viaverse.content.post.application.port.out;

import app.viaverse.content.post.domain.model.ContentInteraction;
import java.util.Set;
import java.util.UUID;

public interface ContentInteractionRepository {
    ContentInteraction save(ContentInteraction interaction);
    Set<UUID> findSuppressedPostIds(UUID viewerAccountId);
}
