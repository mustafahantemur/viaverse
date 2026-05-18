package app.viaverse.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Limit;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Drains pending {@code outbox_event} rows to Kafka via Spring Cloud Stream.
 *
 * <p>Each scheduler tick claims a batch with {@code SELECT ... FOR UPDATE
 * SKIP LOCKED} so multiple service instances can poll without colliding.
 * On publish success the row is marked SENT; on transient failure the row's
 * {@code available_at} is pushed out with exponential backoff (cap 5 min)
 * and {@code attempts} is incremented. After
 * {@link OutboxDispatcherProperties#getMaxAttempts()} the row is marked
 * FAILED so an operator can investigate without retry storms.
 *
 * <p>The poll interval is set via {@code viaverse.outbox.poll-interval-ms}
 * (default 2000ms). Same prefix in every service.
 */
@Component
public class OutboxKafkaDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxKafkaDispatcher.class);
    private static final Duration MAX_BACKOFF = Duration.ofMinutes(5);

    private final OutboxEventJpaRepository repository;
    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;
    private final OutboxDispatcherProperties properties;
    private final Clock clock;

    public OutboxKafkaDispatcher(
            OutboxEventJpaRepository repository,
            StreamBridge streamBridge,
            ObjectMapper objectMapper,
            OutboxDispatcherProperties properties,
            Clock clock
    ) {
        this.repository = repository;
        this.streamBridge = streamBridge;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${viaverse.outbox.poll-interval-ms:2000}")
    @Transactional
    public void drain() {
        Instant now = clock.instant();
        List<OutboxEventJpaEntity> batch =
                repository.findByStatusAndAvailableAtLessThanEqualOrderByAvailableAtAsc(
                        OutboxEventStatusEnum.PENDING,
                        now,
                        Limit.of(properties.getBatchSize())
                );
        for (OutboxEventJpaEntity row : batch) {
            try {
                Map<String, String> headers = readHeaders(row.getHeadersJson());
                String binding = headers.get(OutboxEventWriter.HEADER_DESTINATION_BINDING);
                String partitionKey = headers.get(OutboxEventWriter.HEADER_PARTITION_KEY);
                Object payload = objectMapper.readValue(row.getPayloadJson(), Object.class);
                boolean accepted = streamBridge.send(binding, MessageBuilder
                        .withPayload(payload)
                        .setHeader(MessageHeaders.CONTENT_TYPE, "application/json")
                        .setHeader("kafka_messageKey",
                                partitionKey == null || partitionKey.isBlank() ? row.getId().toString() : partitionKey)
                        .setHeader("eventId", row.getId().toString())
                        .setHeader("eventType", row.getEventType())
                        .build());
                if (!accepted) {
                    throw new IllegalStateException("StreamBridge refused message");
                }
                row.markSent(clock.instant());
            } catch (Exception exception) {
                handleFailure(row, exception);
            }
        }
    }

    private void handleFailure(OutboxEventJpaEntity row, Exception exception) {
        int nextAttempt = row.getAttempts() + 1;
        String error = exception.getClass().getSimpleName() + ": " + exception.getMessage();
        if (nextAttempt >= properties.getMaxAttempts()) {
            LOGGER.error("outbox event {} permanently failed after {} attempts: {}",
                    row.getId(), nextAttempt, error, exception);
            row.markFailedTerminally(error, clock.instant());
            return;
        }
        Duration backoff = backoffFor(nextAttempt);
        LOGGER.warn("outbox event {} transient publish failure (attempt {}/{}); retrying in {}s: {}",
                row.getId(), nextAttempt, properties.getMaxAttempts(), backoff.toSeconds(), error);
        row.markFailureAndReschedule(error, clock.instant().plus(backoff), clock.instant());
    }

    private Duration backoffFor(int attempt) {
        long seconds = (long) Math.min(MAX_BACKOFF.toSeconds(), Math.pow(2, attempt));
        return Duration.ofSeconds(Math.max(1, seconds));
    }

    private Map<String, String> readHeaders(String headersJson) throws Exception {
        if (headersJson == null || headersJson.isBlank()) {
            return Map.of();
        }
        return objectMapper.readValue(
                headersJson,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
        );
    }
}
