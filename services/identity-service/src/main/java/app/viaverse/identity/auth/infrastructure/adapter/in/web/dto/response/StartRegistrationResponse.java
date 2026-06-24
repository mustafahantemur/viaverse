package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import java.time.Instant;
import java.util.UUID;

public record StartRegistrationResponse(
        AuthNextStepEnum nextStep,
        UUID draftId,
        UUID emailFlowId,
        Instant emailExpiresAt,
        boolean phoneVerificationPending
) {
    public static StartRegistrationResponse emailRequired(
            UUID draftId,
            UUID emailFlowId,
            Instant emailExpiresAt,
            boolean phoneVerificationPending
    ) {
        return new StartRegistrationResponse(
                AuthNextStepEnum.EMAIL_VERIFICATION_REQUIRED,
                draftId,
                emailFlowId,
                emailExpiresAt,
                phoneVerificationPending
        );
    }
}
