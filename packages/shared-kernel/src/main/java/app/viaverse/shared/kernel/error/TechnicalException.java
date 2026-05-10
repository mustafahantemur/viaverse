package app.viaverse.shared.kernel.error;

public final class TechnicalException extends AppException {
    public TechnicalException(String message) {
        super(AppErrorCode.TECHNICAL_ERROR, message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(AppErrorCode.TECHNICAL_ERROR, message, cause);
    }
}

