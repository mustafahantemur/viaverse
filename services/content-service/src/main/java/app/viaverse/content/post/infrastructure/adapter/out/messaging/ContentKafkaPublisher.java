package app.viaverse.content.post.infrastructure.adapter.out.messaging;

import app.viaverse.content.post.application.port.out.ContentEventPublisher;
import app.viaverse.content.post.domain.model.ContentInteraction;
import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.contracts.content.ContentEventTypes;
import app.viaverse.contracts.content.ContentInteractionRecordedV1KafkaEvent;
import app.viaverse.contracts.content.ContentPostCreatedV1KafkaEvent;
import app.viaverse.contracts.content.ContentPostPublishedV1KafkaEvent;
import app.viaverse.messaging.outbox.OutboxEventWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ContentKafkaPublisher implements ContentEventPublisher {

    private static final String BINDING_NAME = "contentEvents-out-0";

    private final OutboxEventWriter outboxWriter;
    private final Clock clock;

    public ContentKafkaPublisher(OutboxEventWriter outboxWriter, Clock clock) {
        this.outboxWriter = outboxWriter;
        this.clock = clock;
    }

    @Override
    public void publishPostCreated(ContentPost post) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ContentEventTypes.POST_CREATED_V1,
                BINDING_NAME,
                post.getId().toString(),
                new ContentPostCreatedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        post.getId(),
                        post.getAuthorAccountId(),
                        post.getPostType().name(),
                        post.getCity(),
                        post.getDistrict(),
                        post.getMediaAssetIds()
                )
        );
    }

    @Override
    public void publishPostPublished(ContentPost post) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ContentEventTypes.POST_PUBLISHED_V1,
                BINDING_NAME,
                post.getId().toString(),
                new ContentPostPublishedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        post.getId(),
                        post.getAuthorAccountId(),
                        post.getPostType().name(),
                        post.getCity(),
                        post.getDistrict(),
                        post.getPublishedAt()
                )
        );
    }

    @Override
    public void publishInteractionRecorded(ContentInteraction interaction) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ContentEventTypes.INTERACTION_RECORDED_V1,
                BINDING_NAME,
                interaction.getViewerAccountId().toString(),
                new ContentInteractionRecordedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        interaction.getId(),
                        interaction.getViewerAccountId(),
                        interaction.getPostId(),
                        interaction.getSignalType().name(),
                        interaction.getSurface(),
                        interaction.getPosition(),
                        interaction.getDwellTimeMs(),
                        interaction.getSessionId()
                )
        );
    }
}
