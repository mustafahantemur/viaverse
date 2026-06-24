package app.viaverse.messaging.outbox;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Keeps the outbox persistence slice self-contained when a service adds the kernel.
 */
@Configuration
@EntityScan(basePackages = "app.viaverse")
@EnableJpaRepositories(basePackages = "app.viaverse")
public class MessagingKernelJpaConfiguration {
}
