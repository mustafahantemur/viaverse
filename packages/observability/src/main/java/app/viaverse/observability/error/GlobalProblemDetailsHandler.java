package app.viaverse.observability.error;

import app.viaverse.observability.correlation.CorrelationIdProvider;
import app.viaverse.observability.correlation.CorrelationIds;
import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.AppException;
import app.viaverse.shared.kernel.error.ConflictException;
import app.viaverse.shared.kernel.error.ForbiddenException;
import app.viaverse.shared.kernel.error.NotFoundException;
import app.viaverse.shared.kernel.error.TechnicalException;
import app.viaverse.shared.kernel.error.UnauthorizedException;
import app.viaverse.shared.kernel.error.ValidationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class GlobalProblemDetailsHandler {
    private final CorrelationIdProvider correlationIdProvider = new CorrelationIdProvider();

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidation(ValidationException exception) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, exception.errorCode(), exception.getMessage());
        if (!exception.fieldErrors().isEmpty()) {
            problem.setProperty("fieldErrors", exception.fieldErrors());
        }
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problem = problem(
                HttpStatus.BAD_REQUEST,
                AppErrorCode.VALIDATION_FAILED,
                "Validation failed"
        );
        problem.setProperty("fieldErrors", fieldErrors);
        return problem;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.errorCode(), exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorized(UnauthorizedException exception) {
        return problem(HttpStatus.UNAUTHORIZED, exception.errorCode(), exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException exception) {
        return problem(HttpStatus.FORBIDDEN, exception.errorCode(), exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException exception) {
        return problem(HttpStatus.CONFLICT, exception.errorCode(), exception.getMessage());
    }

    @ExceptionHandler(TechnicalException.class)
    public ProblemDetail handleTechnical(TechnicalException exception) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, exception.errorCode(), "Technical service error");
    }

    @ExceptionHandler(AppException.class)
    public ProblemDetail handleAppException(AppException exception) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, exception.errorCode(), "Application service error");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, AppErrorCode.TECHNICAL_ERROR, "Unexpected service error");
    }

    protected ProblemDetail problem(HttpStatus status, AppErrorCode errorCode, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("code", errorCode.name());
        String correlationId = correlationIdProvider.currentCorrelationId();
        if (correlationId != null && !correlationId.isBlank()) {
            problem.setProperty(CorrelationIds.PROBLEM_PROPERTY, correlationId);
        }
        String requestId = correlationIdProvider.currentRequestId();
        if (requestId != null && !requestId.isBlank()) {
            problem.setProperty(CorrelationIds.REQUEST_PROBLEM_PROPERTY, requestId);
        }
        return problem;
    }
}
