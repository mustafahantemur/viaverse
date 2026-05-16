package app.viaverse.identity.config;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.ServiceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Wires OpenTelemetry resource attributes and span sampling for identity-service.
 *
 * <p>OTLP exporter endpoints, metric registry and the Micrometer Tracing -> OTel bridge
 * are auto-configured by Spring Boot via {@code management.otlp.*} properties; this class
 * only contributes the {@link Resource} (service identity) and a profile-aware
 * {@link Sampler} so we can flip between AlwaysOn in dev/test and parent-based ratio
 * sampling in production.
 */
@Configuration
public class OpenTelemetryConfiguration {

    private static final String SERVICE_NAME = "identity-service";

    private final String serviceVersion;
    private final String deploymentEnvironment;
    private final double samplingProbability;
    private final boolean isProductionLike;

    public OpenTelemetryConfiguration(
            Environment environment,
            @Value("${spring.application.version:dev}") String serviceVersion,
            @Value("${VIAVERSE_ENV:local}") String deploymentEnvironment,
            @Value("${management.tracing.sampling.probability:1.0}") double samplingProbability) {
        this.serviceVersion = serviceVersion;
        this.deploymentEnvironment = deploymentEnvironment;
        this.samplingProbability = samplingProbability;
        this.isProductionLike = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("prod")
                        || profile.equalsIgnoreCase("production")
                        || profile.equalsIgnoreCase("staging"));
    }

    /**
     * Service identity attached to every span/metric/log emitted by this process.
     * Spring Boot's OTel auto-configuration merges this with its default Resource.
     */
    @Bean
    public Resource openTelemetryResource() {
        return Resource.getDefault().merge(
                Resource.create(
                        io.opentelemetry.api.common.Attributes.builder()
                                .put(ServiceAttributes.SERVICE_NAME, SERVICE_NAME)
                                .put(ServiceAttributes.SERVICE_VERSION, serviceVersion)
                                .put(AttributeKey.stringKey("deployment.environment.name"), deploymentEnvironment)
                                .build()));
    }

    /**
     * AlwaysOn in local/test, ParentBased(TraceIdRatio) in prod-like profiles.
     * Spring Boot picks this bean up automatically for the OTel SdkTracerProvider.
     */
    @Bean
    public Sampler openTelemetrySampler() {
        if (!isProductionLike) {
            return Sampler.alwaysOn();
        }
        return Sampler.parentBased(Sampler.traceIdRatioBased(samplingProbability));
    }

    @Bean
    public Tracer identityTracer() {
        return GlobalOpenTelemetry.getTracer(SERVICE_NAME, serviceVersion);
    }

    /**
     * Tag all meters with the service name so dashboards can filter by service.
     */
    @Bean
    public MeterFilter identityServiceMeterTag() {
        return MeterFilter.commonTags(io.micrometer.core.instrument.Tags.of("service.name", SERVICE_NAME));
    }
}
