package app.viaverse.marketplace.config;

import app.viaverse.observability.error.GlobalProblemDetailsHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler extends GlobalProblemDetailsHandler {
}

