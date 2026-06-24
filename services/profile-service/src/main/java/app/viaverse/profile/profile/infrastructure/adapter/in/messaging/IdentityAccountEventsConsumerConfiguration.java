package app.viaverse.profile.profile.infrastructure.adapter.in.messaging;

import app.viaverse.contracts.identity.account.AccountCreatedV1KafkaEvent;
import app.viaverse.contracts.identity.account.IdentityAccountEventTypes;
import app.viaverse.profile.profile.application.port.in.ProvisionProfileFromAccountCreatedUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class IdentityAccountEventsConsumerConfiguration {

    @Bean
    public Consumer<Message<Map<String, Object>>> identityAccountEventsConsumer(
            ProvisionProfileFromAccountCreatedUseCase useCase,
            ObjectMapper objectMapper
    ) {
        return message -> {
            Object eventType = message.getHeaders().get("eventType");
            if (!IdentityAccountEventTypes.ACCOUNT_CREATED_V1.equals(eventType)) {
                return;
            }
            AccountCreatedV1KafkaEvent event =
                    objectMapper.convertValue(message.getPayload(), AccountCreatedV1KafkaEvent.class);
            useCase.provision(new ProvisionProfileFromAccountCreatedUseCase.Command(
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
