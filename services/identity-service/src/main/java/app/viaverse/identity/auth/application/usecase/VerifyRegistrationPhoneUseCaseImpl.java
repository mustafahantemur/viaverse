package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.auth.application.port.in.VerifyRegistrationPhoneUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.RegistrationDraftStore;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.application.service.RegistrationCompletionService;
import app.viaverse.identity.auth.application.service.RegistrationCompletionService.VerifiedIdentifier;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.RegistrationDraft;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyRegistrationPhoneUseCaseImpl implements VerifyRegistrationPhoneUseCase {

    private final Clock clock;
    private final AuthLoginFlowRepository flowRepository;
    private final OtpChallengeService otpChallengeService;
    private final RegistrationDraftStore draftStore;
    private final RegistrationCompletionService registrationCompletionService;
    private final AuthAbuseProtectionService abuseProtectionService;

    public VerifyRegistrationPhoneUseCaseImpl(
            Clock clock,
            AuthLoginFlowRepository flowRepository,
            OtpChallengeService otpChallengeService,
            RegistrationDraftStore draftStore,
            RegistrationCompletionService registrationCompletionService,
            AuthAbuseProtectionService abuseProtectionService
    ) {
        this.clock = clock;
        this.flowRepository = flowRepository;
        this.otpChallengeService = otpChallengeService;
        this.draftStore = draftStore;
        this.registrationCompletionService = registrationCompletionService;
        this.abuseProtectionService = abuseProtectionService;
    }

    @Override
    @ObservedAction("auth.register.verify_phone")
    @AuditEvent(IdentityAuditEventEnum.OTP_VERIFIED)
    @Transactional
    public Result execute(Command command) {
        Instant now = clock.instant();
        RegistrationDraft draft = draftStore.findById(command.draftId())
                .orElseThrow(IdentityErrors::registrationDraftMissing);
        if (!draft.isEmailVerified()) {
            throw IdentityErrors.registrationDraftWrongStage(
                    "Email verification must complete before phone verification");
        }
        if (!draft.hasPhone() || draft.getPhoneFlowId() == null) {
            throw IdentityErrors.registrationDraftWrongStage(
                    "This draft has no pending phone verification");
        }
        AuthLoginFlow phoneFlow = flowRepository.findById(draft.getPhoneFlowId())
                .orElseThrow(IdentityErrors::registrationDraftMissing);
        if (phoneFlow.getPurpose() != LoginFlowPurposeEnum.REGISTRATION) {
            throw IdentityErrors.registrationDraftWrongStage(
                    "Underlying flow is not a registration flow");
        }
        AuthLoginFlow emailFlow = flowRepository.findById(draft.getEmailFlowId())
                .orElseThrow(IdentityErrors::registrationDraftMissing);

        abuseProtectionService.enforceOtpAttempt(phoneFlow, command.clientIp());
        if (!otpChallengeService.verifyOtpForFlow(phoneFlow, command.otp(), now)) {
            flowRepository.save(phoneFlow);
            throw IdentityErrors.invalidOtp();
        }
        flowRepository.save(phoneFlow);

        draft.markPhoneVerified(now);
        RegistrationCompletionService.Completed completed = registrationCompletionService
                .createVerifiedAccount(
                        draft.getDisplayName(),
                        draft.getFirstName(),
                        draft.getLastName(),
                        draft.getPasswordHash(),
                        List.of(
                                new VerifiedIdentifier(IdentifierTypeEnum.EMAIL, draft.getEmail()),
                                new VerifiedIdentifier(IdentifierTypeEnum.PHONE, draft.getPhone())
                        ),
                        draft.getAcceptedRequiredConsents(),
                        draft.isMarketingConsentAccepted(),
                        Set.of(AccountRoleEnum.USER),
                        List.of(emailFlow, phoneFlow),
                        command.userAgent(),
                        command.clientIp(),
                        now
                );
        draftStore.delete(draft.getId());

        return new Result(
                AuthNextStepEnum.AUTHENTICATED,
                completed.accountId(),
                completed.issued().session().getId(),
                completed.issued().accessToken(),
                completed.issued().accessTokenExpiresAt(),
                completed.issued().refreshToken(),
                completed.issued().refreshTokenExpiresAt()
        );
    }
}

