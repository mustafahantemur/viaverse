package app.viaverse.identity.shared.logging;

import app.viaverse.observability.correlation.CorrelationIds;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.AppException;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public final class ObservedActionAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservedActionAspect.class);

    @Around("@annotation(observedAction)")
    public Object observe(ProceedingJoinPoint joinPoint, ObservedAction observedAction) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            log(observedAction.value(), "success", null, startedAt, null);
            return result;
        } catch (AppException exception) {
            log(observedAction.value(), "failure", exception.errorCode(), startedAt, null);
            throw exception;
        } catch (Throwable throwable) {
            log(observedAction.value(), "error", AppErrorCode.TECHNICAL_ERROR, startedAt, throwable);
            throw throwable;
        } finally {
            ActionLogContext.clear();
        }
    }

    private void log(
            String action,
            String outcome,
            AppErrorCode errorCode,
            long startedAt,
            Throwable throwable
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
        for (Map.Entry<String, Object> entry : ActionLogContext.snapshot().entrySet()) {
            builder.addKeyValue(entry.getKey(), entry.getValue());
        }
        if (throwable != null) {
            builder.setCause(throwable);
        }
        builder.log(action + " " + outcome);
    }
}
