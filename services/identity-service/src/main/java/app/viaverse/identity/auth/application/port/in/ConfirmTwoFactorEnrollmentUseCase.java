package app.viaverse.identity.auth.application.port.in;

import java.util.List;
import java.util.UUID;

public interface ConfirmTwoFactorEnrollmentUseCase {

    Result execute(Command command);

    record Command(
            UUID accountId,
            UUID flowId,
            String otp,
            String totpCode,
            String clientIp
    ) {}

    record Result(List<String> backupCodes) {}
}
