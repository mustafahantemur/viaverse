package app.viaverse.content.post.application.usecase;

import app.viaverse.content.post.application.port.in.CreateContentPostUseCase;
import app.viaverse.content.post.application.port.out.ContentEventPublisher;
import app.viaverse.content.post.application.port.out.ContentPostRepository;
import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateContentPostUseCaseImpl implements CreateContentPostUseCase {

    private final ContentPostRepository repository;
    private final ContentEventPublisher eventPublisher;
    private final Clock clock;

    public CreateContentPostUseCaseImpl(
            ContentPostRepository repository,
            ContentEventPublisher eventPublisher,
            Clock clock
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("content.post.create")
    @Transactional
    public ContentPost execute(Command command) {
        ContentPost saved = repository.save(ContentPost.publish(
                command.authorAccountId(),
                command.authorMode(),
                command.postType(),
                command.title(),
                command.body(),
                command.city(),
                command.district(),
                command.eventStartsAt(),
                command.eventEndsAt(),
                command.mediaAssetIds(),
                clock.instant()
        ));
        eventPublisher.publishPostCreated(saved);
        eventPublisher.publishPostPublished(saved);
        return saved;
    }
}
