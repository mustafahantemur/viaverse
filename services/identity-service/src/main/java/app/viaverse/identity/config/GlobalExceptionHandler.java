package app.viaverse.identity.config;

import app.viaverse.identity.shared.error.IdentityException;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import app.viaverse.observability.error.GlobalProblemDetailsHandler;
import app.viaverse.shared.kernel.error.AppErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler extends GlobalProblemDetailsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IdentityException.class)
    public ProblemDetail handleIdentity(IdentityException exception) {
        LOGGER.atWarn()
                .addKeyValue("event.action", "identity.error")
                .addKeyValue("event.outcome", "failure")
                .addKeyValue("error.code", exception.errorCode())
                .addKeyValue("http.response.status_code", exception.status().value())
                .log("identity request failed");
        ProblemDetail problem = problem(exception.status(), publicCode(exception.status()), exception.getMessage());
        problem.setProperty("identityCode", exception.errorCode().name());
        if (!exception.fieldErrors().isEmpty()) {
            problem.setProperty("fieldErrors", exception.fieldErrors());
        }
        return problem;
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimited(RateLimitExceededException exception, HttpServletResponse response) {
        LOGGER.atWarn()
                .addKeyValue("event.action", "identity.rate_limit")
                .addKeyValue("event.outcome", "rate_limited")
                .addKeyValue("error.code", exception.errorCode())
                .addKeyValue("retry_after_seconds", exception.retryAfterSeconds())
                .log("identity request rate_limited");
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
