package app.viaverse.messaging.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tunables for the outbox dispatcher. Properties live under
 * {@code viaverse.outbox.*} — same prefix in every service so operators
 * don't have to relearn the knobs per backend.
 */
@ConfigurationProperties(prefix = "viaverse.outbox")
public class OutboxDispatcherProperties {

    /** Maximum rows claimed per poll tick. */
    private int batchSize = 100;

    /** After this many publish attempts the row is marked FAILED for ops review. */
    private int maxAttempts = 8;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
