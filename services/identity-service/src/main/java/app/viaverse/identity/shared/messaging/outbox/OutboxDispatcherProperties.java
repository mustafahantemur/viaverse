package app.viaverse.identity.shared.messaging.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.identity.outbox")
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
