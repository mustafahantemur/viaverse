package app.viaverse.shared.kernel.error;

public final class TechnicalException extends AppException {
    public TechnicalException(String message) {
        super(AppErrorCode.TECHNICAL_ERROR, message);
    }

    public TechnicalException(AppErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(AppErrorCode.TECHNICAL_ERROR, message, cause);
    }

    public TechnicalException(AppErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
