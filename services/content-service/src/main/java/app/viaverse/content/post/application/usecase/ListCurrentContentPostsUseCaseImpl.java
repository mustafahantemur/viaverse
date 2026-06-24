package app.viaverse.content.post.application.usecase;

import app.viaverse.content.post.application.port.in.ListCurrentContentPostsUseCase;
import app.viaverse.content.post.application.port.out.ContentPostRepository;
import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.web.logging.ObservedAction;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListCurrentContentPostsUseCaseImpl implements ListCurrentContentPostsUseCase {

    private final ContentPostRepository repository;

    public ListCurrentContentPostsUseCaseImpl(ContentPostRepository repository) {
        this.repository = repository;
    }

    @Override
    @ObservedAction("content.post.list_current")
    public List<ContentPost> execute(UUID authorAccountId) {
        return repository.findAllByAuthorAccountId(authorAccountId);
    }
}
