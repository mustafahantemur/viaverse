package app.viaverse.shared.kernel.error;

public final class ConflictException extends AppException {
    public ConflictException(String message) {
        super(AppErrorCode.CONFLICT, message);
    }
}

