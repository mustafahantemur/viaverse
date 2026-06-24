package app.viaverse.content.post.application.port.in;

import app.viaverse.content.post.domain.model.ContentPost;
import java.util.List;
import java.util.UUID;

public interface ListCurrentContentPostsUseCase {
    List<ContentPost> execute(UUID authorAccountId);
}
