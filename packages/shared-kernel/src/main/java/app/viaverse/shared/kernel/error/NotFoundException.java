package app.viaverse.shared.kernel.error;

public final class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(AppErrorCode.NOT_FOUND, message);
    }
}

