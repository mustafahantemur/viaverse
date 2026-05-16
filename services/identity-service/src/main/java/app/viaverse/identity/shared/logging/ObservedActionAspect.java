package app.viaverse.identity.shared.logging;

import app.viaverse.observability.correlation.CorrelationIds;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.AppException;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public final class ObservedActionAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservedActionAspect.class);
    private final Tracer tracer;

    public ObservedActionAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("@annotation(observedAction)")
    public Object observe(ProceedingJoinPoint joinPoint, ObservedAction observedAction) throws Throwable {
        long startedAt = System.nanoTime();
        Span span = tracer.spanBuilder(observedAction.value()).startSpan();
        try {
            span.setAttribute("event.action", observedAction.value());
            captureLogParams(joinPoint);
            try (var ignored = span.makeCurrent()) {
                Object result = joinPoint.proceed();
                span.setAttribute("event.outcome", "success");
                log(observedAction.value(), "success", null, startedAt, null, span);
                return result;
            }
        } catch (AppException exception) {
            span.setAttribute("event.outcome", "failure");
            span.setAttribute("error.code", exception.errorCode().name());
            span.setStatus(StatusCode.ERROR);
            log(observedAction.value(), "failure", exception.errorCode(), startedAt, null, span);
            throw exception;
        } catch (Throwable throwable) {
            span.setAttribute("event.outcome", "error");
            span.setAttribute("error.code", AppErrorCode.TECHNICAL_ERROR.name());
            span.recordException(throwable);
            span.setStatus(StatusCode.ERROR);
            log(observedAction.value(), "error", AppErrorCode.TECHNICAL_ERROR, startedAt, throwable, span);
            throw throwable;
        } finally {
            span.end();
            ActionLogContext.clear();
        }
    }

    private void captureLogParams(ProceedingJoinPoint joinPoint) {
        if (!(joinPoint.getSignature() instanceof MethodSignature methodSignature)) {
            return;
        }
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof LogParam logParam && args[i] != null) {
                    ActionLogContext.put(logParam.value(), args[i]);
                }
            }
        }
    }

    private void log(
            String action,
            String outcome,
            AppErrorCode errorCode,
            long startedAt,
            Throwable throwable,
            Span span
    ) {
        long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
        LoggingEventBuilder builder = ("success".equals(outcome) ? LOGGER.atInfo() : LOGGER.atWarn())
                .addKeyValue("event.action", action)
                .addKeyValue("event.outcome", outcome)
                .addKeyValue("duration_ms", durationMs);
        if (errorCode != null) {
            builder.addKeyValue("error.code", errorCode);
        }
        if (throwable instanceof RateLimitExceededException rateLimited) {
            builder.addKeyValue("retry_after_seconds", rateLimited.retryAfterSeconds());
        }
        String correlationId = MDC.get(CorrelationIds.MDC_KEY);
        if (correlationId != null && !correlationId.isBlank()) {
            builder.addKeyValue("correlation.id", correlationId);
        }
        String requestId = MDC.get(CorrelationIds.REQUEST_MDC_KEY);
        if (requestId != null && !requestId.isBlank()) {
            builder.addKeyValue("request.id", requestId);
        }
        if (span.getSpanContext().isValid()) {
            builder.addKeyValue("trace.id", span.getSpanContext().getTraceId());
            builder.addKeyValue("span.id", span.getSpanContext().getSpanId());
        }
        for (Map.Entry<String, Object> entry : ActionLogContext.snapshot().entrySet()) {
            builder.addKeyValue(entry.getKey(), entry.getValue());
        }
        builder.log(action + " " + outcome);
    }
}
