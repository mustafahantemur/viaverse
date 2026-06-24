package app.viaverse.shared.kernel.error;

public abstract class AppException extends RuntimeException {
    private final AppErrorCode errorCode;

    protected AppException(AppErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected AppException(AppErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AppErrorCode errorCode() {
        return errorCode;
    }
}

