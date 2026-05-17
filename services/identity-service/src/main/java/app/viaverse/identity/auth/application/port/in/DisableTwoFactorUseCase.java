package app.viaverse.identity.auth.application.port.in;

import java.util.UUID;

public interface DisableTwoFactorUseCase {

    void execute(Command command);

    record Command(
            UUID accountId,
            UUID flowId,
            String otp,
            String totpCode,
            String backupCode,
            String clientIp
    ) {}
}
