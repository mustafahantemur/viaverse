package app.viaverse.content.post.application.usecase;

import app.viaverse.content.post.application.port.in.RecordContentInteractionUseCase;
import app.viaverse.content.post.application.port.out.ContentEventPublisher;
import app.viaverse.content.post.application.port.out.ContentInteractionRepository;
import app.viaverse.content.post.application.port.out.ContentPostRepository;
import app.viaverse.content.post.domain.model.ContentInteraction;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordContentInteractionUseCaseImpl implements RecordContentInteractionUseCase {
    private final ContentPostRepository postRepository;
    private final ContentInteractionRepository interactionRepository;
    private final ContentEventPublisher eventPublisher;
    private final Clock clock;

    public RecordContentInteractionUseCaseImpl(
            ContentPostRepository postRepository,
            ContentInteractionRepository interactionRepository,
            ContentEventPublisher eventPublisher,
            Clock clock
    ) {
        this.postRepository = postRepository;
        this.interactionRepository = interactionRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @ObservedAction("content.interaction.record")
    @Transactional
    public ContentInteraction execute(Command command) {
        postRepository.findById(command.postId())
                .orElseThrow(() -> new NotFoundException("Content post not found"));
        ContentInteraction saved = interactionRepository.save(ContentInteraction.record(
                command.viewerAccountId(),
                command.postId(),
                command.signalType(),
                command.surface(),
                command.position(),
                command.dwellTimeMs(),
                command.sessionId(),
                command.occurredAt() == null ? clock.instant() : command.occurredAt(),
                clock.instant()
        ));
        eventPublisher.publishInteractionRecorded(saved);
        return saved;
    }
}
