package app.viaverse.content.post.application.port.out;

import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.content.post.domain.model.ContentInteraction;

public interface ContentEventPublisher {
    void publishPostCreated(ContentPost post);
    void publishPostPublished(ContentPost post);
    void publishInteractionRecorded(ContentInteraction interaction);
}
