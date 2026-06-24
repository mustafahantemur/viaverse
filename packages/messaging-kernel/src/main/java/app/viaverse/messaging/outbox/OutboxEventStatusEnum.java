package app.viaverse.messaging.outbox;

/** Lifecycle state of a row in {@code outbox_event}. */
public enum OutboxEventStatusEnum {
    /** Awaiting publish. */
    PENDING,
    /** Successfully published to Kafka. */
    SENT,
    /** Exceeded retry budget — kept for operator triage, not retried. */
    FAILED
}
