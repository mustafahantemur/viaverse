package app.viaverse.identity.config;

import app.viaverse.identity.application.auth.RateLimitExceededException;
import app.viaverse.observability.error.GlobalProblemDetailsHandler;
import app.viaverse.shared.kernel.error.AppErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler extends GlobalProblemDetailsHandler {
    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimited(RateLimitExceededException exception, HttpServletResponse response) {
        response.setHeader("Retry-After", Long.toString(exception.retryAfterSeconds()));
        ProblemDetail problem = problem(
                HttpStatus.TOO_MANY_REQUESTS,
                AppErrorCode.RATE_LIMITED,
                "Too many authentication attempts"
        );
        problem.setProperty("retryAfterSeconds", exception.retryAfterSeconds());
        return problem;
    }
}
