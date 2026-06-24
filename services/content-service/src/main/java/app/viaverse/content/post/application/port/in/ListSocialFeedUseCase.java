package app.viaverse.content.post.application.port.in;

import app.viaverse.content.post.application.model.SocialFeedItem;
import java.util.List;
import java.util.UUID;

public interface ListSocialFeedUseCase {
    List<SocialFeedItem> execute(Command command);

    record Command(
            UUID viewerAccountId,
            String city,
            String district
    ) {
    }
}
