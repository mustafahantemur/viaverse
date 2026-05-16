package app.viaverse.identity.account.infrastructure.adapter.out.messaging;

import app.viaverse.identity.account.application.port.out.AccountEventPublisher;
import app.viaverse.identity.account.domain.AccountStatusEnum;
import app.viaverse.identity.account.infrastructure.adapter.out.messaging.event.AccountCreatedV1KafkaEvent;
import app.viaverse.identity.account.infrastructure.adapter.out.messaging.event.AccountStatusChangedV1KafkaEvent;
import java.time.Instant;
import java.util.UUID;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class AccountKafkaPublisher implements AccountEventPublisher {

    private static final String BINDING_NAME = "identityAccountEvents-out-0";

    private final StreamBridge streamBridge;

    public AccountKafkaPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void publishCreated(UUID accountId, String displayName) {
        publish(new AccountCreatedV1KafkaEvent(
                UUID.randomUUID(),
                Instant.now(),
                "v1",
                accountId,
                displayName
        ));
    }

    @Override
    public void publishStatusChanged(UUID accountId, AccountStatusEnum newStatus) {
        publish(new AccountStatusChangedV1KafkaEvent(
                UUID.randomUUID(),
                Instant.now(),
                "v1",
                accountId,
                newStatus
        ));
    }

    private void publish(Object event) {
        if (!streamBridge.send(BINDING_NAME, event)) {
            throw new IllegalStateException("Failed to publish account event");
        }
    }
}
