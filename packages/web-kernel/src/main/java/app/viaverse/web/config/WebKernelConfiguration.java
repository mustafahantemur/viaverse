package app.viaverse.web.config;

import app.viaverse.web.http.HttpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HttpProperties.class)
public class WebKernelConfiguration {
}
