package app.viaverse.observability.config;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.ServiceAttributes;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenTelemetryConfiguration {

    private final String serviceName;
    private final String serviceVersion;
    private final String deploymentEnvironment;
    private final double samplingProbability;
    private final boolean isProductionLike;

    public OpenTelemetryConfiguration(
            Environment environment,
            @Value("${spring.application.name}") String serviceName,
            @Value("${spring.application.version:dev}") String serviceVersion,
            @Value("${VIAVERSE_ENV:local}") String deploymentEnvironment,
            @Value("${management.tracing.sampling.probability:1.0}") double samplingProbability
    ) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.deploymentEnvironment = deploymentEnvironment;
        this.samplingProbability = samplingProbability;
        this.isProductionLike = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("prod")
                        || profile.equalsIgnoreCase("production")
                        || profile.equalsIgnoreCase("staging"));
    }

    @Bean
    public Resource openTelemetryResource() {
        return Resource.getDefault().merge(
                Resource.create(
                        io.opentelemetry.api.common.Attributes.builder()
                                .put(ServiceAttributes.SERVICE_NAME, serviceName)
                                .put(ServiceAttributes.SERVICE_VERSION, serviceVersion)
                                .put(AttributeKey.stringKey("deployment.environment"), deploymentEnvironment)
                                .build()));
    }

    @Bean
    public Sampler openTelemetrySampler() {
        if (!isProductionLike) {
            return Sampler.alwaysOn();
        }
        return Sampler.parentBased(Sampler.traceIdRatioBased(samplingProbability));
    }

    @Bean
    public Tracer applicationTracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName, serviceVersion);
    }

    @Bean
    public ApplicationRunner installOpenTelemetryLogbackAppender(OpenTelemetry openTelemetry) {
        return args -> OpenTelemetryAppender.install(openTelemetry);
    }

    @Bean
    public MeterFilter serviceNameMeterTag() {
        return MeterFilter.commonTags(Tags.of("service.name", serviceName));
    }
}
