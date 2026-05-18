package app.viaverse.content.post.application.usecase;

import app.viaverse.content.post.application.port.in.ListPublishedContentPostsUseCase;
import app.viaverse.content.post.application.port.out.ContentPostRepository;
import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListPublishedContentPostsUseCaseImpl implements ListPublishedContentPostsUseCase {

    private final ContentPostRepository repository;

    public ListPublishedContentPostsUseCaseImpl(ContentPostRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("content.post.list_published")
    public List<ContentPost> execute(String city, String district) {
        return repository.findAllPublished(city, district);
    }
}
