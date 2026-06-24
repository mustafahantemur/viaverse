package app.viaverse.messaging.outbox;

import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.TechnicalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Writes events to the {@code outbox_event} table. Callers are expected to be
 * inside a JPA transaction so the event row commits atomically with the
 * domain state change that produced it ("transactional outbox" pattern).
 *
 * <p>The {@link OutboxKafkaDispatcher} drains pending rows to Kafka in a
 * background thread.
 */
@Component
public class OutboxEventWriter {

    public static final String HEADER_PARTITION_KEY = "partitionKey";
    public static final String HEADER_DESTINATION_BINDING = "destinationBinding";

    private final OutboxEventJpaRepository repository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public OutboxEventWriter(OutboxEventJpaRepository repository, ObjectMapper objectMapper, Clock clock) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public void enqueue(
            UUID eventId,
            String eventType,
            String destinationBinding,
            String partitionKey,
            Object payload
    ) {
        Instant now = clock.instant();
        String payloadJson = writeJson(payload);
        String headersJson = writeJson(Map.of(
                HEADER_PARTITION_KEY, partitionKey == null ? "" : partitionKey,
                HEADER_DESTINATION_BINDING, destinationBinding
        ));
        repository.save(new OutboxEventJpaEntity(
                eventId,
                eventType,
                payloadJson,
                headersJson,
                OutboxEventStatusEnum.PENDING,
                now,
                now,
                now
        ));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            // Serialization failure here means the payload has a Jackson
            // mapping bug — that's a code defect, not a user-facing condition.
            throw new TechnicalException(
                    AppErrorCode.TECHNICAL_ERROR,
                    "Failed to serialize outbox event payload",
                    exception);
        }
    }
}
