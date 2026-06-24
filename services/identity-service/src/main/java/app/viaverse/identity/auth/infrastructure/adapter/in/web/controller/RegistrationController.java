package app.viaverse.identity.auth.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.account.application.port.in.GetCurrentAccountUseCase;
import app.viaverse.identity.account.infrastructure.adapter.in.web.mapper.AccountDtoMapper;
import app.viaverse.identity.auth.application.port.in.StartRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyRegistrationEmailUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyRegistrationPhoneUseCase;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.StartRegistrationRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.VerifyRegistrationOtpRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.AuthResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.StartRegistrationResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.VerifyRegistrationEmailResponse;
import app.viaverse.web.api.ApiResponse;
import app.viaverse.web.security.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The draft-based, form-first registration flow.
 *
 * <ol>
 *   <li>{@code POST /start} — the user submits the whole form. Server
 *       validates everything, hashes the password, stashes the draft in
 *       Valkey, dispatches an email OTP, returns the {@code draftId}.</li>
 *   <li>{@code POST /verify-email} — the user submits the email OTP.
 *       If no phone was given, the account is created and full session
 *       tokens come back. If a phone <em>was</em> given, a phone OTP is
 *       dispatched and {@code PHONE_VERIFICATION_REQUIRED} is returned.</li>
 *   <li>{@code POST /verify-phone} — the user submits the phone OTP.
 *       Account is created with both identifiers verified; tokens come
 *       back.</li>
 * </ol>
 *
 * <p>Lives separately from {@link AuthController} so the multi-step
 * registration surface is locally readable. Admin invitation registration
 * still uses the older single-token path on AuthController.
 */
@RestController
@RequestMapping("/api/v1/auth/register")
public class RegistrationController {

    private final StartRegistrationUseCase startRegistrationUseCase;
    private final VerifyRegistrationEmailUseCase verifyRegistrationEmailUseCase;
    private final VerifyRegistrationPhoneUseCase verifyRegistrationPhoneUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final AccountDtoMapper accountDtoMapper;
    private final ClientIpResolver clientIpResolver;

    public RegistrationController(
            StartRegistrationUseCase startRegistrationUseCase,
            VerifyRegistrationEmailUseCase verifyRegistrationEmailUseCase,
            VerifyRegistrationPhoneUseCase verifyRegistrationPhoneUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            AccountDtoMapper accountDtoMapper,
            ClientIpResolver clientIpResolver
    ) {
        this.startRegistrationUseCase = startRegistrationUseCase;
        this.verifyRegistrationEmailUseCase = verifyRegistrationEmailUseCase;
        this.verifyRegistrationPhoneUseCase = verifyRegistrationPhoneUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.accountDtoMapper = accountDtoMapper;
        this.clientIpResolver = clientIpResolver;
    }

    @PostMapping("/start")
    public ApiResponse<StartRegistrationResponse> start(
            @Valid @RequestBody StartRegistrationRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Client-Fingerprint", required = false) String clientFingerprint,
            HttpServletRequest httpRequest
    ) {
        StartRegistrationUseCase.Result result = startRegistrationUseCase.execute(
                new StartRegistrationUseCase.Command(
                        request.email(),
                        request.phone(),
                        request.displayName(),
                        request.firstName(),
                        request.lastName(),
                        request.password(),
                        request.acceptedRequiredConsents(),
                        request.marketingConsentAccepted(),
                        clientIpResolver.resolve(httpRequest),
                        clientFingerprint,
                        userAgent
                )
        );
        return ApiResponse.ok(StartRegistrationResponse.emailRequired(
                result.draftId(),
                result.emailFlowId(),
                result.emailExpiresAt(),
                result.phoneRequiredAfterEmail()
        ));
    }

    @PostMapping("/verify-email")
    public ApiResponse<VerifyRegistrationEmailResponse> verifyEmail(
            @Valid @RequestBody VerifyRegistrationOtpRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        VerifyRegistrationEmailUseCase.Result result = verifyRegistrationEmailUseCase.execute(
                new VerifyRegistrationEmailUseCase.Command(
                        request.draftId(),
                        request.otp(),
                        clientIpResolver.resolve(httpRequest),
                        userAgent
                )
        );
        return ApiResponse.ok(toEmailResponse(result));
    }

    @PostMapping("/verify-phone")
    public ApiResponse<AuthResponse> verifyPhone(
            @Valid @RequestBody VerifyRegistrationOtpRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        VerifyRegistrationPhoneUseCase.Result result = verifyRegistrationPhoneUseCase.execute(
                new VerifyRegistrationPhoneUseCase.Command(
                        request.draftId(),
                        request.otp(),
                        clientIpResolver.resolve(httpRequest),
                        userAgent
                )
        );
        return ApiResponse.ok(new AuthResponse(
                AuthNextStepEnum.AUTHENTICATED,
                result.accessToken(),
                result.accessTokenExpiresAt(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                currentAccountView(result.accountId(), result.sessionId())
        ));
    }

    private VerifyRegistrationEmailResponse toEmailResponse(VerifyRegistrationEmailUseCase.Result result) {
        if (result.nextStep() == AuthNextStepEnum.AUTHENTICATED) {
            return new VerifyRegistrationEmailResponse(
                    AuthNextStepEnum.AUTHENTICATED,
                    null, null,
                    result.accessToken(),
                    result.accessTokenExpiresAt(),
                    result.refreshToken(),
                    result.refreshTokenExpiresAt(),
                    currentAccountView(result.accountId(), result.sessionId())
            );
        }
        return new VerifyRegistrationEmailResponse(
                AuthNextStepEnum.PHONE_VERIFICATION_REQUIRED,
                result.phoneFlowId(),
                result.phoneExpiresAt(),
                null, null, null, null, null
        );
    }

    private app.viaverse.identity.account.domain.AccountView currentAccountView(UUID accountId, UUID sessionId) {
        var account = getCurrentAccountUseCase.execute(new GetCurrentAccountUseCase.Command(accountId, sessionId));
        return accountDtoMapper.toView(account);
    }
}

