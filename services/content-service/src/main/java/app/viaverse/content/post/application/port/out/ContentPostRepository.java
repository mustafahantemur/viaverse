package app.viaverse.content.post.application.port.out;

import app.viaverse.content.post.domain.model.ContentPost;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentPostRepository {
    ContentPost save(ContentPost post);
    Optional<ContentPost> findById(UUID postId);
    List<ContentPost> findAllByAuthorAccountId(UUID authorAccountId);
    List<ContentPost> findAllPublished(String city, String district);
}
