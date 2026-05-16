package app.viaverse.identity.shared.aspect;

import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.logging.LogParam;
import app.viaverse.observability.audit.AuditAction;
import app.viaverse.observability.audit.AuditActor;
import app.viaverse.observability.audit.AuditContext;
import app.viaverse.observability.audit.AuditLogger;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Emits an identity audit event when a method annotated with
 * {@link AuditEvent} returns successfully. The account id is resolved from
 * the returned {@link AuditableResult} or, as a fallback, from a parameter
 * annotated with {@code @LogParam("user.id")}.
 */
@Aspect
@Component
public final class AuditEventAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventAspect.class);
    private static final String USER_ID_PARAM_KEY = "user.id";

    private final AuditLogger auditLogger;

    public AuditEventAspect(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @AfterReturning(pointcut = "@annotation(auditEvent)", returning = "result")
    public void recordAuditEvent(JoinPoint joinPoint, AuditEvent auditEvent, Object result) {
        UUID accountId = resolveAccountId(joinPoint, result);
        if (accountId == null) {
            LOGGER.warn(
                    "@AuditEvent on {} returned without a resolvable accountId; audit event {} skipped",
                    joinPoint.getSignature().toShortString(),
                    auditEvent.value()
            );
            return;
        }
        record(auditEvent.value(), accountId);
    }

    void record(IdentityAuditEventEnum event, UUID accountId) {
        auditLogger.record(new app.viaverse.observability.audit.AuditEvent(
                UUID.randomUUID(),
                Instant.now(),
                new AuditActor("ACCOUNT", accountId.toString()),
                AuditAction.TECHNICAL_ACCESS,
                "identity",
                accountId.toString(),
                new AuditContext(null, null, event.name()),
                Map.of()
        ));
    }

    private UUID resolveAccountId(JoinPoint joinPoint, Object result) {
        if (result instanceof AuditableResult auditable && auditable.accountId() != null) {
            return auditable.accountId();
        }
        if (!(joinPoint.getSignature() instanceof MethodSignature methodSignature)) {
            return null;
        }
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof LogParam logParam
                        && USER_ID_PARAM_KEY.equals(logParam.value())
                        && args[i] instanceof UUID uuid) {
                    return uuid;
                }
            }
        }
        return null;
    }
}
