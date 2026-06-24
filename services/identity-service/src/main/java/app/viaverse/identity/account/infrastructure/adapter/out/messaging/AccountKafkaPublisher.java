package app.viaverse.identity.account.infrastructure.adapter.out.messaging;

import app.viaverse.identity.account.application.port.out.AccountEventPublisher;
import app.viaverse.identity.account.domain.AccountStatusEnum;
import app.viaverse.contracts.identity.account.AccountCreatedV1KafkaEvent;
import app.viaverse.contracts.identity.account.AccountStatusChangedV1KafkaEvent;
import app.viaverse.contracts.identity.account.IdentityAccountEventTypes;
import app.viaverse.messaging.outbox.OutboxEventWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Writes account events to the transactional outbox. The actual Kafka
 * publish happens asynchronously in {@code OutboxKafkaDispatcher}; this
 * adapter only persists the event row in the current JPA transaction so
 * the event is durable atomically with the domain state change.
 */
@Component
public class AccountKafkaPublisher implements AccountEventPublisher {

    private static final String BINDING_NAME = "identityAccountEvents-out-0";
    private final OutboxEventWriter outboxWriter;
    private final Clock clock;

    public AccountKafkaPublisher(OutboxEventWriter outboxWriter, Clock clock) {
        this.outboxWriter = outboxWriter;
        this.clock = clock;
    }

    @Override
    public void publishCreated(UUID accountId, String displayName, String firstName, String lastName) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                IdentityAccountEventTypes.ACCOUNT_CREATED_V1,
                BINDING_NAME,
                accountId.toString(),
                new AccountCreatedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        accountId,
                        displayName,
                        firstName,
                        lastName
                )
        );
    }

    @Override
    public void publishStatusChanged(UUID accountId, AccountStatusEnum newStatus) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                IdentityAccountEventTypes.ACCOUNT_STATUS_CHANGED_V1,
                BINDING_NAME,
                accountId.toString(),
                new AccountStatusChangedV1KafkaEvent(eventId, Instant.now(clock), "v1", accountId, newStatus.name())
        );
    }
}
