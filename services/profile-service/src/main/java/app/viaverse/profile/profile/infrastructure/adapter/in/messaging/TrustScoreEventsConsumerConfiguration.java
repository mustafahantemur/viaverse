package app.viaverse.profile.profile.infrastructure.adapter.in.messaging;

import app.viaverse.contracts.trust.score.TrustEventTypes;
import app.viaverse.contracts.trust.score.TrustScoreUpdatedV1KafkaEvent;
import app.viaverse.profile.profile.application.port.in.SyncTrustScoreFromEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class TrustScoreEventsConsumerConfiguration {

    @Bean
    public Consumer<Message<Map<String, Object>>> trustScoreEventsConsumer(
            SyncTrustScoreFromEventUseCase useCase,
            ObjectMapper objectMapper
    ) {
        return message -> {
            Object eventType = message.getHeaders().get("eventType");
            if (!TrustEventTypes.TRUST_SCORE_UPDATED_V1.equals(eventType)) {
                return;
            }
            TrustScoreUpdatedV1KafkaEvent event =
                    objectMapper.convertValue(message.getPayload(), TrustScoreUpdatedV1KafkaEvent.class);
            useCase.sync(new SyncTrustScoreFromEventUseCase.Command(
                    event.eventId(),
                    event.occurredAt(),
                    event.accountId(),
                    event.score(),
                    event.level(),
                    event.badge()
            ));
        };
    }
}
