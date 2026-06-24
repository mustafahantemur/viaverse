package app.viaverse.identity.config;

import app.viaverse.identity.shared.error.IdentityException;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import app.viaverse.observability.error.GlobalProblemDetailsHandler;
import app.viaverse.shared.kernel.error.AppErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler extends GlobalProblemDetailsHandler {
    @ExceptionHandler(IdentityException.class)
    public ProblemDetail handleIdentity(IdentityException exception) {
        ProblemDetail problem = problem(exception.status(), publicCode(exception.status()), exception.getMessage());
        problem.setProperty("identityCode", exception.errorCode().name());
        if (!exception.fieldErrors().isEmpty()) {
            problem.setProperty("fieldErrors", exception.fieldErrors());
        }
        return problem;
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimited(RateLimitExceededException exception, HttpServletResponse response) {
        response.setHeader("Retry-After", Long.toString(exception.retryAfterSeconds()));
        ProblemDetail problem = problem(
                exception.status(),
                AppErrorCode.RATE_LIMITED,
                exception.getMessage()
        );
        problem.setProperty("identityCode", exception.errorCode().name());
        problem.setProperty("retryAfterSeconds", exception.retryAfterSeconds());
        return problem;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDenied(AuthorizationDeniedException exception) {
        return problem(HttpStatus.FORBIDDEN, AppErrorCode.FORBIDDEN, "Forbidden");
    }

    private AppErrorCode publicCode(HttpStatus status) {
        if (status == HttpStatus.BAD_REQUEST) {
            return AppErrorCode.VALIDATION_FAILED;
        }
        if (status == HttpStatus.UNAUTHORIZED) {
            return AppErrorCode.UNAUTHORIZED;
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return AppErrorCode.RATE_LIMITED;
        }
        return AppErrorCode.TECHNICAL_ERROR;
    }
}
