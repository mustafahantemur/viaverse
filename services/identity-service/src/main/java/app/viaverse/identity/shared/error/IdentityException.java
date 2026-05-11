package app.viaverse.identity.shared.error;

import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.AppException;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class IdentityException extends AppException {
    private final HttpStatus status;
    private final Map<String, String> fieldErrors;

    public IdentityException(AppErrorCode errorCode, String message, HttpStatus status) {
        this(errorCode, message, status, Map.of());
    }

    public IdentityException(
            AppErrorCode errorCode,
            String message,
            HttpStatus status,
            Map<String, String> fieldErrors
    ) {
        super(errorCode, message);
        this.status = status;
        this.fieldErrors = Map.copyOf(fieldErrors);
    }

    public HttpStatus status() {
        return status;
    }

    public Map<String, String> fieldErrors() {
        return fieldErrors;
    }
}
