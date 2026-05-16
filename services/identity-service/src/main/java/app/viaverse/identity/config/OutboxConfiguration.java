package app.viaverse.identity.config;

import app.viaverse.identity.shared.messaging.outbox.OutboxDispatcherProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutboxDispatcherProperties.class)
public class OutboxConfiguration {
}
