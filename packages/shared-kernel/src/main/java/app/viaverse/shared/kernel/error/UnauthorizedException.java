package app.viaverse.shared.kernel.error;

public final class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(AppErrorCode.UNAUTHORIZED, message);
    }
}

