package app.viaverse.identity.shared.aspect;

import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.error.RefreshTokenReuseDetectedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public final class RefreshTokenReuseAspect {

    private final AuditEventAspect auditEventAspect;

    public RefreshTokenReuseAspect(AuditEventAspect auditEventAspect) {
        this.auditEventAspect = auditEventAspect;
    }

    @Around("execution(* app.viaverse.identity.auth.application..*(..))")
    public Object recordReuseAndRethrow(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (RefreshTokenReuseDetectedException reuse) {
            auditEventAspect.record(IdentityAuditEventEnum.REFRESH_TOKEN_REUSE_DETECTED, reuse.accountId());
            throw IdentityErrors.invalidRefreshToken();
        }
    }
}
