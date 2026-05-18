package app.viaverse.trustgamification.trust.infrastructure.adapter.in.messaging;

import app.viaverse.contracts.profile.profile.ProfileCreatedV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileEventTypes;
import app.viaverse.trustgamification.trust.application.port.in.BootstrapTrustFromProfileCreatedUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class ProfileEventsConsumerConfiguration {

    @Bean
    public Consumer<Message<Map<String, Object>>> profileEventsConsumer(
            BootstrapTrustFromProfileCreatedUseCase useCase,
            ObjectMapper objectMapper
    ) {
        return message -> {
            Object eventType = message.getHeaders().get("eventType");
            if (!ProfileEventTypes.PROFILE_CREATED_V1.equals(eventType)) {
                return;
            }
            ProfileCreatedV1KafkaEvent event =
                    objectMapper.convertValue(message.getPayload(), ProfileCreatedV1KafkaEvent.class);
            useCase.bootstrap(new BootstrapTrustFromProfileCreatedUseCase.Command(
                    event.eventId(),
                    event.occurredAt(),
                    event.accountId()
            ));
        };
    }
}
