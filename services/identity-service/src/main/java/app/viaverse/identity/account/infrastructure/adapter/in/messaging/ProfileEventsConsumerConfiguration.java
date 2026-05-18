package app.viaverse.identity.account.infrastructure.adapter.in.messaging;

import app.viaverse.contracts.profile.profile.ProfileEventTypes;
import app.viaverse.contracts.profile.profile.ProfileUpdatedV1KafkaEvent;
import app.viaverse.identity.account.application.port.in.MirrorProfileUpdatedUseCase;
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
            MirrorProfileUpdatedUseCase useCase,
            ObjectMapper objectMapper
    ) {
        return message -> {
            Object eventType = message.getHeaders().get("eventType");
            if (!ProfileEventTypes.PROFILE_UPDATED_V1.equals(eventType)) {
                return;
            }
            ProfileUpdatedV1KafkaEvent event =
                    objectMapper.convertValue(message.getPayload(), ProfileUpdatedV1KafkaEvent.class);
            useCase.mirror(new MirrorProfileUpdatedUseCase.Command(
                    event.eventId(),
                    event.occurredAt(),
                    event.accountId(),
                    event.displayName(),
                    event.firstName(),
                    event.lastName()
            ));
        };
    }
}
