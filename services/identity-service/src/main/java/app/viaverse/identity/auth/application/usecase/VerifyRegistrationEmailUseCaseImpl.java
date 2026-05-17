package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.auth.application.port.in.VerifyRegistrationEmailUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.RegistrationDraftStore;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.application.service.RegistrationCompletionService;
import app.viaverse.identity.auth.application.service.RegistrationCompletionService.VerifiedIdentifier;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.RegistrationDraft;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyRegistrationEmailUseCaseImpl implements VerifyRegistrationEmailUseCase {

    private final java.time.Clock clock;
    private final AuthProperties properties;
    private final AuthLoginFlowRepository flowRepository;
    private final OtpChallengeService otpChallengeService;
    private final RegistrationDraftStore draftStore;
    private final RegistrationCompletionService registrationCompletionService;
    private final AuthAbuseProtectionService abuseProtectionService;

    public VerifyRegistrationEmailUseCaseImpl(
            java.time.Clock clock,
            AuthProperties properties,
            AuthLoginFlowRepository flowRepository,
            OtpChallengeService otpChallengeService,
            RegistrationDraftStore draftStore,
            RegistrationCompletionService registrationCompletionService,
            AuthAbuseProtectionService abuseProtectionService
    ) {
        this.clock = clock;
        this.properties = properties;
        this.flowRepository = flowRepository;
        this.otpChallengeService = otpChallengeService;
        this.draftStore = draftStore;
        this.registrationCompletionService = registrationCompletionService;
        this.abuseProtectionService = abuseProtectionService;
    }

    @Override
    @ObservedAction("auth.register.verify_email")
    @AuditEvent(IdentityAuditEventEnum.OTP_VERIFIED)
    @Transactional
    public Result execute(Command command) {
        Instant now = clock.instant();
        RegistrationDraft draft = draftStore.findById(command.draftId())
                .orElseThrow(IdentityErrors::registrationDraftMissing);
        if (draft.isEmailVerified()) {
            throw IdentityErrors.registrationDraftWrongStage(
                    "Email is already verified for this draft");
        }
        AuthLoginFlow emailFlow = flowRepository.findById(draft.getEmailFlowId())
                .orElseThrow(IdentityErrors::registrationDraftMissing);
        if (emailFlow.getPurpose() != LoginFlowPurposeEnum.REGISTRATION) {
            throw IdentityErrors.registrationDraftWrongStage(
                    "Underlying flow is not a registration flow");
        }

        abuseProtectionService.enforceOtpAttempt(emailFlow, command.clientIp());
        if (!otpChallengeService.verifyOtpForFlow(emailFlow, command.otp(), now)) {
            flowRepository.save(emailFlow);
            throw IdentityErrors.invalidOtp();
        }
        flowRepository.save(emailFlow);

        draft.markEmailVerified(now);
        if (!draft.hasPhone()) {
            // No phone → create the account right now and end the flow.
            RegistrationCompletionService.Completed completed = registrationCompletionService
                    .createVerifiedAccount(
                            draft.getDisplayName(),
                            draft.getFirstName(),
                            draft.getLastName(),
                            draft.getPasswordHash(),
                            List.of(new VerifiedIdentifier(IdentifierTypeEnum.EMAIL, draft.getEmail())),
                            draft.getAcceptedRequiredConsents(),
                            draft.isMarketingConsentAccepted(),
                            Set.of(AccountRoleEnum.USER),
                            List.of(emailFlow),
                            command.userAgent(),
                            command.clientIp(),
                            now
                    );
            draftStore.delete(draft.getId());
            return new Result(
                    AuthNextStepEnum.AUTHENTICATED,
                    null, null,
                    completed.accountId(),
                    completed.issued().session().getId(),
                    completed.issued().accessToken(),
                    completed.issued().accessTokenExpiresAt(),
                    completed.issued().refreshToken(),
                    completed.issued().refreshTokenExpiresAt()
            );
        }

        // Phone present → start the phone OTP flow and persist the draft.
        NormalizedIdentifier phone = new NormalizedIdentifier(IdentifierTypeEnum.PHONE, draft.getPhone());
        Instant phoneExpiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlow phoneFlow = flowRepository.save(AuthLoginFlow.issue(
                UUID.randomUUID(),
                LoginFlowPurposeEnum.REGISTRATION,
                phone.type(),
                phone.value(),
                null,
                phoneExpiresAt,
                now
        ));
        abuseProtectionService.enforceOtpResendCooldown(phone);
        otpChallengeService.issue(phoneFlow.getId(), phone, phoneExpiresAt, now);
        draft.attachPhoneFlow(phoneFlow.getId());
        // Keep the draft alive at least as long as the phone OTP is usable;
        // floor at 5 minutes so a slow click between email and phone OTPs
        // doesn't strand the user with an expired draft and a valid OTP.
        java.time.Duration draftTtl = java.time.Duration.between(now, phoneExpiresAt)
                .plus(java.time.Duration.ofMinutes(5));
        draftStore.save(draft, draftTtl);

        return new Result(
                AuthNextStepEnum.PHONE_VERIFICATION_REQUIRED,
                phoneFlow.getId(),
                phoneExpiresAt,
                null, null, null, null, null, null
        );
    }
}
