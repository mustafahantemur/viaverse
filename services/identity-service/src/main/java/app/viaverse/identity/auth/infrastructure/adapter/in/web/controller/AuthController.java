package app.viaverse.identity.auth.infrastructure.adapter.in.web.controller;

import app.viaverse.identity.account.application.port.in.GetCurrentAccountUseCase;
import app.viaverse.identity.account.infrastructure.adapter.in.web.mapper.AccountDtoMapper;
import app.viaverse.identity.auth.application.port.in.CompleteAdminRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.LogoutUseCase;
import app.viaverse.identity.auth.application.port.in.PasswordLoginUseCase;
import app.viaverse.identity.auth.application.port.in.RefreshTokenUseCase;
import app.viaverse.identity.auth.application.port.in.SocialSignInUseCase;
import app.viaverse.identity.auth.application.port.in.StartAuthUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyOtpUseCase;
import app.viaverse.identity.auth.application.port.in.VerifyTotpUseCase;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.AdminRegisterRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.LogoutRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.PasswordLoginRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.RefreshRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.RegisterRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.SocialSignInRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.StartAuthRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.VerifyOtpRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.request.VerifyTotpRequest;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.AuthCompletionResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.AuthResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.CapabilityTermsResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.RequiredConsentsResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response.StartAuthResponse;
import app.viaverse.identity.auth.infrastructure.adapter.in.web.mapper.AuthDtoMapper;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipalResolver;
import app.viaverse.identity.consent.application.ConsentPolicy;
import app.viaverse.web.api.ApiResponse;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.web.security.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth endpoints. Intended client flow:
 *
 * <ol>
 *   <li><b>First touch on an identifier</b> → {@code POST /start} returns
 *       {@code PASSWORD_REQUIRED} (known account) or {@code OTP_REQUIRED}
 *       (new account; OTP dispatched out-of-band).</li>
 *   <li><b>Known account</b> → {@code POST /password-login} with email +
 *       password. Returns {@code AUTHENTICATED}, or {@code TOTP_REQUIRED}
 *       when 2FA is on (closed by {@code POST /verify-totp}).</li>
 *   <li><b>New account</b> → {@code POST /verify-otp} consumes the OTP and
 *       returns a registration token. {@code POST /register} accepts the
 *       token plus display name, password (required for OTP flow, optional
 *       on social flow), and consents.</li>
 *   <li><b>Social sign-in</b> → {@code POST /social/{provider}} replaces
 *       start + password-login for users who registered via Google / Apple.
 *       Returns the same {@code TOTP_REQUIRED} branch when 2FA is enabled.</li>
 *   <li><b>Subsequent app launches</b> → {@code POST /refresh}. Clients
 *       persist the refresh token (30-day TTL) and exchange it for a new
 *       access + refresh pair on every cold start — no OTP, no password,
 *       no TOTP.</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final StartAuthUseCase startAuthUseCase;
    private final SocialSignInUseCase socialSignInUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;
    private final PasswordLoginUseCase passwordLoginUseCase;
    private final VerifyTotpUseCase verifyTotpUseCase;
    private final CompleteRegistrationUseCase completeRegistrationUseCase;
    private final CompleteAdminRegistrationUseCase completeAdminRegistrationUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final JwtPrincipalResolver jwtPrincipalResolver;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final AccountDtoMapper accountDtoMapper;
    private final AuthDtoMapper authDtoMapper;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final ClientIpResolver clientIpResolver;
    private final ConsentPolicy consentPolicy;

    public AuthController(
            StartAuthUseCase startAuthUseCase,
            SocialSignInUseCase socialSignInUseCase,
            VerifyOtpUseCase verifyOtpUseCase,
            PasswordLoginUseCase passwordLoginUseCase,
            VerifyTotpUseCase verifyTotpUseCase,
            CompleteRegistrationUseCase completeRegistrationUseCase,
            CompleteAdminRegistrationUseCase completeAdminRegistrationUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            JwtPrincipalResolver jwtPrincipalResolver,
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            AccountDtoMapper accountDtoMapper,
            AuthDtoMapper authDtoMapper,
            AuthAbuseProtectionService abuseProtectionService,
            ClientIpResolver clientIpResolver,
            ConsentPolicy consentPolicy
    ) {
        this.startAuthUseCase = startAuthUseCase;
        this.socialSignInUseCase = socialSignInUseCase;
        this.verifyOtpUseCase = verifyOtpUseCase;
        this.passwordLoginUseCase = passwordLoginUseCase;
        this.verifyTotpUseCase = verifyTotpUseCase;
        this.completeRegistrationUseCase = completeRegistrationUseCase;
        this.completeAdminRegistrationUseCase = completeAdminRegistrationUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.jwtPrincipalResolver = jwtPrincipalResolver;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.accountDtoMapper = accountDtoMapper;
        this.authDtoMapper = authDtoMapper;
        this.abuseProtectionService = abuseProtectionService;
        this.clientIpResolver = clientIpResolver;
        this.consentPolicy = consentPolicy;
    }

    @GetMapping("/required-consents")
    public ApiResponse<RequiredConsentsResponse> requiredConsents() {
        return ApiResponse.ok(new RequiredConsentsResponse(
                consentPolicy.requiredDocuments(),
                consentPolicy.marketingDocument()
        ));
    }

    @GetMapping("/capability-terms")
    public ApiResponse<CapabilityTermsResponse> capabilityTerms() {
        return ApiResponse.ok(new CapabilityTermsResponse(java.util.List.of(
                consentPolicy.providerTermsDocument(),
                consentPolicy.businessTermsDocument()
        )));
    }

    @PostMapping("/start")
    public ApiResponse<StartAuthResponse> start(
            @Valid @RequestBody StartAuthRequest request,
            @RequestHeader(value = "X-Client-Fingerprint", required = false) String clientFingerprint,
            HttpServletRequest httpRequest
    ) {
        StartAuthUseCase.Result result = startAuthUseCase.execute(new StartAuthUseCase.Command(
                request.identifier(),
                clientIpResolver.resolve(httpRequest),
                clientFingerprint
        ));
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/password-login")
    public ApiResponse<AuthCompletionResponse> passwordLogin(
            @Valid @RequestBody PasswordLoginRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        PasswordLoginUseCase.Result result = passwordLoginUseCase.execute(
                new PasswordLoginUseCase.Command(
                        request.identifier(),
                        request.password(),
                        userAgent,
                        clientIpResolver.resolve(httpRequest)
                )
        );
        if (result.nextStep() == AuthNextStepEnum.AUTHENTICATED) {
            return ApiResponse.ok(authDtoMapper.toAuthResponse(
                    result,
                    currentAccountView(result.accountId(), result.sessionId())
            ));
        }
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/verify-totp")
    public ApiResponse<AuthResponse> verifyTotp(
            @Valid @RequestBody VerifyTotpRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        VerifyTotpUseCase.Result result = verifyTotpUseCase.execute(
                new VerifyTotpUseCase.Command(
                        request.partialAuthToken(),
                        request.totpCode(),
                        userAgent,
                        clientIpResolver.resolve(httpRequest)
                )
        );
        return ApiResponse.ok(authDtoMapper.toAuthResponse(
                result,
                currentAccountView(result.accountId(), result.sessionId())
        ));
    }

    @PostMapping("/verify-otp")
    public ApiResponse<AuthCompletionResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpServletRequest httpRequest
    ) {
        VerifyOtpUseCase.Result result = verifyOtpUseCase.execute(new VerifyOtpUseCase.Command(
                request.flowId(),
                request.otp(),
                clientIpResolver.resolve(httpRequest)
        ));
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/social/{provider}")
    public ApiResponse<AuthCompletionResponse> socialSignIn(
            @PathVariable SocialAuthProviderEnum provider,
            @Valid @RequestBody SocialSignInRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Client-Fingerprint", required = false) String clientFingerprint,
            HttpServletRequest httpRequest
    ) {
        SocialSignInUseCase.Result result = socialSignInUseCase.execute(new SocialSignInUseCase.Command(
                provider,
                request.idToken(),
                request.nonce(),
                userAgent,
                clientIpResolver.resolve(httpRequest),
                clientFingerprint
        ));
        if (result.nextStep() == AuthNextStepEnum.AUTHENTICATED) {
            return ApiResponse.ok(authDtoMapper.toAuthResponse(
                    result,
                    currentAccountView(result.accountId(), result.sessionId())
            ));
        }
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        CompleteRegistrationUseCase.Result result = completeRegistrationUseCase.execute(
                new CompleteRegistrationUseCase.Command(
                        request.registrationToken(),
                        request.displayName(),
                        request.firstName(),
                        request.lastName(),
                        request.password(),
                        request.acceptedRequiredConsents(),
                        request.marketingConsentAccepted(),
                        userAgent,
                        clientIpResolver.resolve(httpRequest)
                )
        );
        return ApiResponse.ok(authDtoMapper.toAuthResponse(
                result,
                currentAccountView(result.accountId(), result.sessionId())
        ));
    }

    @PostMapping("/register-admin")
    public ApiResponse<AuthResponse> registerAdmin(
            @Valid @RequestBody AdminRegisterRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        CompleteAdminRegistrationUseCase.Result result = completeAdminRegistrationUseCase.execute(
                new CompleteAdminRegistrationUseCase.Command(
                        request.invitationToken(),
                        request.registrationToken(),
                        request.displayName(),
                        request.firstName(),
                        request.lastName(),
                        request.password(),
                        request.acceptedRequiredConsents(),
                        request.marketingConsentAccepted(),
                        userAgent,
                        clientIpResolver.resolve(httpRequest)
                )
        );
        return ApiResponse.ok(authDtoMapper.toAuthResponse(
                result,
                currentAccountView(result.accountId(), result.sessionId())
        ));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest httpRequest
    ) {
        String clientIp = clientIpResolver.resolve(httpRequest);
        abuseProtectionService.enforceRefresh(clientIp);
        RefreshTokenUseCase.Result result = refreshTokenUseCase.execute(new RefreshTokenUseCase.Command(
                request.refreshToken(),
                userAgent,
                clientIp
        ));
        return ApiResponse.ok(authDtoMapper.toResponse(result));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody(required = false) LogoutRequest request,
            HttpServletRequest httpRequest
    ) {
        abuseProtectionService.enforceLogout(clientIpResolver.resolve(httpRequest));
        boolean noPrincipal = (jwt == null);
        boolean noRefresh = (request == null || request.refreshToken() == null || request.refreshToken().isBlank());
        if (noPrincipal && noRefresh) {
            throw IdentityErrors.refreshTokenRequired();
        }
        JwtPrincipal principal = jwt == null ? null : jwtPrincipalResolver.resolve(jwt);
        logoutUseCase.execute(new LogoutUseCase.Command(
                principal == null ? null : principal.accountId(),
                principal == null ? null : principal.sessionId(),
                request == null ? null : request.refreshToken()
        ));
        return ApiResponse.ok(null);
    }

    private app.viaverse.identity.account.domain.AccountView currentAccountView(
            java.util.UUID accountId,
            java.util.UUID sessionId
    ) {
        var account = getCurrentAccountUseCase.execute(new GetCurrentAccountUseCase.Command(accountId, sessionId));
        return accountDtoMapper.toView(account);
    }
}

