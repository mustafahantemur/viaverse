package app.viaverse.content.post.application.port.in;

import app.viaverse.content.post.domain.model.ContentPost;
import java.util.List;

public interface ListPublishedContentPostsUseCase {
    List<ContentPost> execute(String city, String district);
}
