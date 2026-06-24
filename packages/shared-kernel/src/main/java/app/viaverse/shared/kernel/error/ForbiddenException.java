package app.viaverse.shared.kernel.error;

public final class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(AppErrorCode.FORBIDDEN, message);
    }
}

