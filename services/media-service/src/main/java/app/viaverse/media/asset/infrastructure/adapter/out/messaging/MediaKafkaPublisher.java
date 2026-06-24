package app.viaverse.media.asset.infrastructure.adapter.out.messaging;

import app.viaverse.contracts.media.MediaAssetReadyV1KafkaEvent;
import app.viaverse.contracts.media.MediaEventTypes;
import app.viaverse.media.asset.application.port.out.MediaEventPublisher;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.messaging.outbox.OutboxEventWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MediaKafkaPublisher implements MediaEventPublisher {
    private static final String BINDING_NAME = "mediaEvents-out-0";
    private final OutboxEventWriter outboxWriter;
    private final Clock clock;

    public MediaKafkaPublisher(OutboxEventWriter outboxWriter, Clock clock) {
        this.outboxWriter = outboxWriter;
        this.clock = clock;
    }

    @Override
    public void publishAssetReady(MediaAsset asset) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                MediaEventTypes.ASSET_READY_V1,
                BINDING_NAME,
                asset.getId().toString(),
                new MediaAssetReadyV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        asset.getId(),
                        asset.getOwnerAccountId(),
                        asset.getAssetKind().name(),
                        asset.getContentType(),
                        asset.getByteSize() == null ? 0 : asset.getByteSize()
                )
        );
    }
}
